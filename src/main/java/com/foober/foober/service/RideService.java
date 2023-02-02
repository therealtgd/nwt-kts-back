package com.foober.foober.service;

import com.foober.foober.dto.ActiveRideDto;
import com.foober.foober.dto.ReportDto;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.exception.BadRequestException;
import com.foober.foober.exception.ClientUnavailable;
import com.foober.foober.exception.DriverUnavailable;
import com.foober.foober.exception.UnableToGetDriverEtaException;
import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.ClientStatus;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.repos.DriverRepository;
import com.foober.foober.repos.RideRepository;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final int PRICE_PER_KM = 60;
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final ClientRepository clientRepository;

    public RideService(RideRepository rideRepository, DriverRepository driverRepository, ClientRepository clientRepository) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.clientRepository = clientRepository;
    }

    public int getPrice(String vehicleType, int distance) throws IllegalArgumentException {
        return Enum.valueOf(VehicleType.class, vehicleType)
                .getPrice() + (distance / 1000) * PRICE_PER_KM;
    }

    public List<Ride> getRidesNearestToEnd() {
        Optional<List<Ride>> rides = rideRepository.getAllInProgress();
        List<Ride> rideList = new ArrayList<>();
        if (rides.isPresent() && !rides.get().isEmpty()) {
            rides.get().sort((r1, r2) -> {
                Address endOfRoute1 = r1.getRoute().stream().reduce((a, b) -> b).orElse(null);
                Address endOfRoute2 = r2.getRoute().stream().reduce((a, b) -> b).orElse(null);
                if (endOfRoute1 == null || endOfRoute2 == null) {
                    return 0;
                }

                double currentLat1 = r1.getDriver().getVehicle().getLatitude();
                double currentLng1 = r1.getDriver().getVehicle().getLongitude();

                double currentLat2 = r2.getDriver().getVehicle().getLatitude();
                double currentLng2 = r2.getDriver().getVehicle().getLongitude();

                double endLat1 = endOfRoute1.getLatitude();
                double endLng1 = endOfRoute1.getLongitude();
                double endLat2 = endOfRoute2.getLatitude();
                double endLng2 = endOfRoute2.getLongitude();

                try {
                    long timeLeft1 = this.getTimeLeftOnRoute(currentLat1, currentLng1, endLat1, endLng1);
                    long timeLeft2 = this.getTimeLeftOnRoute(currentLat2, currentLng2, endLat2, endLng2);

                    return Long.compare(timeLeft1, timeLeft2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            rideList = rides.get();
        }
        return rideList;
    }

    public ActiveRideDto orderRide(RideInfoDto rideInfoDto) {
        Set<Client> clients = getClients(rideInfoDto);
        Driver driver = getDriver(rideInfoDto);
        Set<Address> route = getRoute(rideInfoDto);
        Ride ride = new Ride(driver, route, rideInfoDto.getPrice(), rideInfoDto.getDistance());
        clients.forEach(ride::addClient);
        try {
            ride.setEta(this.getTimeLeftOnRoute(
                    driver.getVehicle().getLatitude(),
                    driver.getVehicle().getLongitude(),
                    rideInfoDto.getStartAddress().getCoordinates().getLat(),
                    rideInfoDto.getStartAddress().getCoordinates().getLng()));
            rideRepository.save(ride);
            clientRepository.saveAll(clients);
            return new ActiveRideDto(ride);
        } catch (Exception e) {
            throw new UnableToGetDriverEtaException();
        }
    }

    private static Set<Address> getRoute(RideInfoDto rideInfoDto) {
        Set<Address> route = new HashSet<>();
        route.add(new Address(rideInfoDto.getStartAddress(), 0));
        route.addAll(rideInfoDto.getStops().stream()
                .map((a) -> new Address(a, route.size()))
                .toList()
        );
        route.add(new Address(rideInfoDto.getEndAddress(), route.size()));
        Ride ride = new Ride(driver, route, rideInfoDto.getPrice(), rideInfoDto.getDistance());
        clients.forEach(client -> ride.addClient(client));
        rideRepository.save(ride);
        clientRepository.saveAll(clients);
        // TODO: Send notification to Driver
        return route;
    }

    private Driver getDriver(RideInfoDto rideInfoDto) {
        Driver driver = driverRepository.getById(rideInfoDto.getDriver().getId());
        if (!driver.getStatus().equals(DriverStatus.PENDING) && !driver.isReserved()) {
            throw new DriverUnavailable(driver.getDisplayName());
        }
        return driver;
    }

    private Set<Client> getClients(RideInfoDto rideInfoDto) {
        Set<Client> clients = clientRepository.findUsersByUsernames(rideInfoDto.getClients());
        Set<Client> clientsInActiveRide = clients.stream()
                .filter(c -> !c.getStatus().equals(ClientStatus.ONLINE))
                .collect(Collectors.toSet());
        if (!clientsInActiveRide.isEmpty()) {
            throw new ClientUnavailable(clientsInActiveRide.stream()
                    .map(User::getUsername)
                    .collect(Collectors.joining(","))
            );
        }
        return clients;
    }

    private long getTimeLeftOnRoute(double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(this.googleMapsApiKey)
                .build();
        DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(context);
        request
            .origins(originLatitude + "," + originLongitude)
            .destinations(destinationLatitude + "," + destinationLongitude)
            .mode(TravelMode.DRIVING)
            .units(Unit.METRIC);
        DistanceMatrix matrix = request.await();
        return matrix.rows[0].elements[0].duration.inSeconds;
    }

    public ReportDto getClientReport(Client client, String start, String end) {
        Long startTimestamp = stringToDate(start);
        Long endTimestamp = stringToDate(end);
        long nextTimestamp;
        int    sumOfRides = 0,          rides;
        double sumOfDistance = 0,       distance;
        double sumOfTransactions = 0,   transaction;
        ArrayList<Integer> ridesData = new ArrayList<>();
        ArrayList<Double> distanceData = new ArrayList<>();
        ArrayList<Double> transactionsData = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        while (startTimestamp < endTimestamp) {
            nextTimestamp = startTimestamp + 1000 * 60 * 60 * 24;
            List<Ride> ridesList = rideRepository.getRideByStatusAndStartTimeBetweenAndClientsContainingOrderByStartTimeAsc(RideStatus.COMPLETED, startTimestamp, nextTimestamp, client);
            labels.add(dateToString(startTimestamp));
            rides = ridesList.size();
            distance = ridesList.stream().mapToDouble(Ride::getDistance).sum();
            transaction = ridesList.stream().mapToDouble(r -> r.getPrice() / r.getClients().size()).sum();
            ridesData.add(rides);
            distanceData.add(distance);
            transactionsData.add(transaction);
            sumOfRides += rides;
            sumOfDistance += distance;
            sumOfTransactions += transaction;

            startTimestamp = nextTimestamp;
        }

        return new ReportDto(sumOfRides, sumOfDistance, sumOfTransactions,
                Math.round(1.0 * sumOfRides / labels.size() * 100) / 100.0,
                Math.round(sumOfDistance / labels.size() * 100) / 100.0,
                Math.round(sumOfTransactions / labels.size() * 100) / 100.0,
                ridesData, distanceData, transactionsData, labels
        );
    }

    public ReportDto getDriverReport(Driver driver, String start, String end) {
        Long startTimestamp = stringToDate(start);
        Long endTimestamp = stringToDate(end);
        long nextTimestamp;
        int    sumOfRides = 0,          rides;
        double sumOfDistance = 0,       distance;
        double sumOfTransactions = 0,   transaction;
        ArrayList<Integer> ridesData = new ArrayList<>();
        ArrayList<Double> distanceData = new ArrayList<>();
        ArrayList<Double> transactionsData = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        while (startTimestamp < endTimestamp) {
            nextTimestamp = startTimestamp + 1000 * 60 * 60 * 24;
            List<Ride> ridesList = rideRepository.getRideByStatusAndStartTimeBetweenAndDriverOrderByStartTimeAsc(RideStatus.COMPLETED, startTimestamp, nextTimestamp, driver);
            labels.add(dateToString(startTimestamp));
            rides = ridesList.size();
            distance = ridesList.stream().mapToDouble(Ride::getDistance).sum();
            transaction = ridesList.stream().mapToDouble(Ride::getPrice).sum();
            ridesData.add(rides);
            distanceData.add(distance);
            transactionsData.add(transaction);
            sumOfRides += rides;
            sumOfDistance += distance;
            sumOfTransactions += transaction;

            startTimestamp = nextTimestamp;
        }

        return new ReportDto(sumOfRides, sumOfDistance, sumOfTransactions,
                Math.round(1.0 * sumOfRides / labels.size() * 100) / 100.0,
                Math.round(sumOfDistance / labels.size() * 100) / 100.0,
                Math.round(sumOfTransactions / labels.size() * 100) / 100.0,
                ridesData, distanceData, transactionsData, labels
        );
    }

    public ReportDto getAdminReport(String start, String end) {
        Long startTimestamp = stringToDate(start);
        Long endTimestamp = stringToDate(end);
        long nextTimestamp;
        int    sumOfRides = 0,          rides;
        double sumOfDistance = 0,       distance;
        double sumOfTransactions = 0,   transaction;
        ArrayList<Integer> ridesData = new ArrayList<>();
        ArrayList<Double> distanceData = new ArrayList<>();
        ArrayList<Double> transactionsData = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        while (startTimestamp < endTimestamp) {
            nextTimestamp = startTimestamp + 1000 * 60 * 60 * 24;
            List<Ride> ridesList = rideRepository.getRideByStatusAndStartTimeBetweenOrderByStartTimeAsc(RideStatus.COMPLETED, startTimestamp, nextTimestamp);
            labels.add(dateToString(startTimestamp));
            rides = ridesList.size();
            distance = ridesList.stream().mapToDouble(Ride::getDistance).sum();
            transaction = ridesList.stream().mapToDouble(Ride::getPrice).sum();
            ridesData.add(rides);
            distanceData.add(distance);
            transactionsData.add(transaction);
            sumOfRides += rides;
            sumOfDistance += distance;
            sumOfTransactions += transaction;

            startTimestamp = nextTimestamp;
        }

        return new ReportDto(sumOfRides, sumOfDistance, sumOfTransactions,
                Math.round(1.0 * sumOfRides / labels.size() * 100) / 100.0,
                Math.round(sumOfDistance / labels.size() * 100) / 100.0,
                Math.round(sumOfTransactions / labels.size() * 100) / 100.0,
                ridesData, distanceData, transactionsData, labels
        );
    }

    private String dateToString(Long timestamp) {
        Date date=new Date(timestamp);
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM");
        return df2.format(date);
    }
    private long stringToDate(String date) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = f.parse(date);
            return d.getTime();
        } catch (ParseException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public long getDriverEta(Ride ride) {
        double driverLat = ride.getDriver().getVehicle().getLatitude();
        double driverLng = ride.getDriver().getVehicle().getLongitude();
        Optional<Address> address = ride.getRoute().stream().filter(r -> r.getStation() == 0).findFirst();
        if (address.isEmpty()) {
            throw new UnableToGetDriverEtaException();
        }
        double rideLat = address.get().getLatitude();
        double rideLng = address.get().getLongitude();

        try {
            ride.setEta(this.getTimeLeftOnRoute(driverLat, driverLng, rideLat, rideLng));
            ride = rideRepository.save(ride);
            return ride.getEta();
        } catch (Exception e) {
            throw new UnableToGetDriverEtaException();
        }
    }
}
