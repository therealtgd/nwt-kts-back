package com.foober.foober.service;

import com.foober.foober.dto.*;
import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.exception.*;
import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.ClientStatus;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.*;
import com.foober.foober.util.AddressIndexComparator;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class RideService {

    private final int PRICE_PER_KM = 60;
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final ClientRepository clientRepository;
    private final CancellationReasonRepository cancellationRepository;
    private final ReviewRepository reviewRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RideService(RideRepository rideRepository, DriverRepository driverRepository, ClientRepository clientRepository, CancellationReasonRepository cancellationRepository, ReviewRepository reviewRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.clientRepository = clientRepository;
        this.cancellationRepository = cancellationRepository;
        this.reviewRepository = reviewRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public int getPrice(String vehicleType, int distance) throws IllegalArgumentException {
        return Enum.valueOf(VehicleType.class, vehicleType)
                .getPrice() + (distance / 1000) * PRICE_PER_KM;
    }

    public List<Ride> getRidesNearestToEnd() {
        Optional<List<Ride>> rides = rideRepository.getAllInProgressNotReserved();
        List<Ride> rideList = new ArrayList<>();
        if (rides.isPresent() && !rides.get().isEmpty()) {
            rides.get().sort((r1, r2) -> {
                Address endOfRoute1 = r1.getEndAddress();
                Address endOfRoute2 = r2.getEndAddress();
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

    @Transactional
    public ActiveRideDto orderRide(RideInfoDto rideInfoDto, Client client) {
        Set<Client> clients = getClients(rideInfoDto);
        Driver driver = getDriver(rideInfoDto);
        Set<Address> route = getRoute(rideInfoDto);

        Ride ride = new Ride(driver, route, rideInfoDto.getPrice(), rideInfoDto.getDistance());
        clients.forEach(ride::addClient);

        client.setStatus(ClientStatus.IN_RIDE);
        if (clients.size() > 1) {
            // Send notifications to split fare clients, set ride status to PENDING_PAYMENT
            handleSplitFareRide(ride, client);
        } else {
            setRideEta(ride);
        }

        // If the driver is in an active ride, notify them that they have a ride waiting after this one
        if (ride.getStatus() == RideStatus.WAITING) {
            this.simpMessagingTemplate.convertAndSend(
                    "/driver/ride-assigned/" + ride.getDriver().getUsername(),
                    new ActiveRideDto(ride)
            );
        } else if (ride.getClients().size() == 1) { // Else notify the driver that they have a ride and set the ride status to ON_ROUTE and driver status to BUSY
            this.simpMessagingTemplate.convertAndSend(
                    "/driver/active-ride/"+ride.getDriver().getUsername(),
                    new ActiveRideDto(ride)
            );
        }

        clientRepository.saveAll(clients);
        return new ActiveRideDto(ride);
    }

    private void setRideEta(Ride ride) {
        try {
            long eta;
            Driver driver = ride.getDriver();
            if (driver.getStatus().equals(DriverStatus.BUSY)) {
                eta = getBusyDriverEta(ride, driver);
                ride.setStatus(RideStatus.WAITING); // If the driver is already in an active ride, this ride has to WAIT until he completes it
            } else {
                eta = this.getTimeLeftOnRoute(
                        driver.getVehicle().getLatitude(),
                        driver.getVehicle().getLongitude(),
                        ride.getStartAddress().getLatitude(),
                        ride.getStartAddress().getLongitude()
                );
                ride.setStatus(RideStatus.ON_ROUTE);
                driver.setStatus(DriverStatus.BUSY);
            }
            ride.setEta(eta);
            rideRepository.save(ride);
            driverRepository.save(driver);
        } catch (Exception e) {
            throw new UnableToGetDriverEtaException();
        }

    }

    private void handleSplitFareRide(Ride ride, Client client) {
        ride.setStatus(RideStatus.PENDING_PAYMENT);
        ride = rideRepository.save(ride);
        for (Client c: ride.getClients().stream().filter(c -> !Objects.equals(c.getId(), client.getId())).toList()) {
            this.simpMessagingTemplate.convertAndSend(
                    "/client/split-fare/"+c.getUsername(),
                    new ActiveRideDto(ride)
            );
        }
    }

    private long getBusyDriverEta(Ride ride, Driver driver) throws Exception {
        Ride driversRide = rideRepository.getRideByStatusAndDriverId(RideStatus.IN_PROGRESS, driver.getId())
                .orElseThrow(UnableToGetDriverEtaException::new);
        long activeRideTimeUntilEnd = this.getTimeLeftOnRoute(
                driver.getVehicle().getLatitude(),
                driver.getVehicle().getLongitude(),
                driversRide.getEndAddress().getLatitude(),
                driversRide.getEndAddress().getLongitude()
        );
        long newRideDriverEta = this.getTimeLeftOnRoute(
                driversRide.getEndAddress().getLatitude(),
                driversRide.getEndAddress().getLongitude(),
                ride.getStartAddress().getLatitude(),
                ride.getStartAddress().getLongitude()
        );
        return activeRideTimeUntilEnd + newRideDriverEta;
    }

    private static Set<Address> getRoute(RideInfoDto rideInfoDto) {
        Set<Address> route = new HashSet<>();
        route.add(new Address(rideInfoDto.getStartAddress(), 0));
        route.addAll(rideInfoDto.getStops().stream()
                .map((a) -> new Address(a, route.size()))
                .toList()
        );
        route.add(new Address(rideInfoDto.getEndAddress(), route.size()));
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

    public long getDriverEta(User user) {
        Ride ride = rideRepository.getActiveRideByClient((Client) user)
                .orElseThrow(() -> new ClientHasNoActiveRidesException(user.getUsername()));
        if (ride.getEta() <= 0) {
            return 0L;
        }
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

    @Transactional
    public long startRide(long id) {
        Ride ride = rideRepository.getById(id);
        ride.setStatus(RideStatus.IN_PROGRESS);
        long startTime = System.currentTimeMillis();
        ride.setStartTime(startTime);
        rideRepository.save(ride);
        return startTime;
    }

    @Transactional
    public long finishRide(long id) {
        Ride ride = rideRepository.getById(id);
        ride.setStatus(RideStatus.COMPLETED);
        long endTime = System.currentTimeMillis();
        ride.setEndTime(endTime);
        rideRepository.save(ride);
        return endTime;
    }

    public void endRide(long id, RideCancellationDto rideCancellationDto) {
        Ride ride = rideRepository.getById(id);
        ride.getClients().forEach(client -> {
            client.setStatus(ClientStatus.ONLINE);
            client.setCredits((int) Math.round(client.getCredits() + ride.getPrice() / ride.getClients().size()));
        });
        ride.setStatus(RideStatus.CANCELLED);
        CancellationReason cancellationReason = new CancellationReason(rideCancellationDto.getReason(), ride);
        cancellationRepository.save(cancellationReason);
        long endTime = System.currentTimeMillis();
        ride.setEndTime(endTime);
        rideRepository.save(ride);
        clientRepository.saveAll(ride.getClients());
    }

    public Set<Client> getRideClients(long id) {
        return rideRepository.getById(id).getClients();
    }

    public void reviewRide(long rideId, ReviewDto reviewDto, User user) {
        Ride ride = rideRepository.getById(rideId);
        long threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);
        if (ride.getEndTime() <= threeDaysAgo) {
            throw new ReviewPeriodExpired();
        }
        reviewRepository.save(new Review(reviewDto, ride, (Client) user));
    }
    
    public DetailedRideDto getRide(User user, Long id) {
        try {
            Ride ride = rideRepository.getById(id);
            if ((user instanceof Client && ride.getClients().stream().anyMatch(c -> c.getId().equals(user.getId()))) ||
                (user instanceof Driver && ride.getDriver().getId().equals(user.getId()) || user instanceof Admin)) {
                DetailedRideDto dto = new DetailedRideDto(ride);
                dto.setDriverRating(getRideDriverRating(ride));
                dto.setVehicleRating(getRideVehicleRating(ride));
                dto.setReviews(reviewsToDto(reviewRepository.getReviewsByRide(ride)));
                dto.setStops(getStops(ride));
                return dto;
            }
            else {
                throw new NoSuchElementException("You are not authorized to access this ride.");
            }
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("That ride doesn't exist.");
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("That ride doesn't exist.");
        }
    }

    private List<AddressDto> getStops(Ride ride) {
        List<AddressDto> dtos = new ArrayList<>();
        ride.getRoute().stream().sorted(new AddressIndexComparator()).forEach(address -> dtos.add(new AddressDto(address.getStreetAddress(), new LatLng(address.getLatitude(), address.getLongitude()))));
        return dtos;
    }

    private List<ReviewDto> reviewsToDto (List<Review> reviews) {
        ArrayList<ReviewDto> dtos = new ArrayList<>();
        reviews.forEach(r -> dtos.add(new ReviewDto(r)));
        return dtos;
    }

    public  double getRideDriverRating(Ride ride) {
        List<Review> reviews = reviewRepository.getReviewsByRide(ride);
        int sum = reviews.stream().mapToInt(Review::getDriverRating).sum();
        if (sum == 0) {
            return 0;
        }
        else {
            return 1.0 * sum / reviews.size();
        }
    }

    public  double getRideVehicleRating(Ride ride) {
        List<Review> reviews = reviewRepository.getReviewsByRide(ride);
        int sum = reviews.stream().mapToInt(Review::getVehicleRating).sum();
        if (sum == 0) {
            return 0;
        }
        else {
            return 1.0 * sum / reviews.size();
        }
    }


    public void acceptSplitFare(long id) {
        Ride ride = rideRepository.getById(id);
        ride.setSplitFareCounter(ride.getSplitFareCounter()+1);

        // Everyone paid
        if (ride.getSplitFareCounter() == ride.getClients().size() - 1) {
            setRideEta(ride);
            for (Client c: ride.getClients()) {
                c.pay(ride.getSplitFarePrice());
            }
            for (Client c: ride.getClients()) {
                this.simpMessagingTemplate.convertAndSend(
                        "/client/split-fare-ride-started/"+c.getUsername(),
                        new ActiveRideDto(ride)
                );
            }
            this.simpMessagingTemplate.convertAndSend(
                    "/driver/active-ride/"+ride.getDriver().getUsername(),
                    new ActiveRideDto(ride)
            );
            clientRepository.saveAll(ride.getClients());
        }
    }

    public Driver declineSplitFare(long id) {
        Ride ride = this.rideRepository.getById(id);
        ride.setStatus(RideStatus.CANCELLED);
        ride = rideRepository.save(ride);
        return ride.getDriver();
    }

    @Scheduled(fixedRate = 60*1000)
    public void checkReservations() {
        Instant now = Instant.now();
        List<Ride> rides = this.rideRepository.findByStatus(RideStatus.RESERVED);
        for (Ride ride: rides) {
            Instant reserveTime = Instant.ofEpochSecond(ride.getReservationTime());
            Instant before15min = reserveTime.minus(15, ChronoUnit.MINUTES);
            Instant before10min = reserveTime.minus(10, ChronoUnit.MINUTES);
            Instant before5min = reserveTime.minus(5, ChronoUnit.MINUTES);

            if (
                now.isAfter(before15min.minus(30, ChronoUnit.SECONDS)) &&
                    now.isBefore(before15min.plus(30, ChronoUnit.SECONDS))
            ) {
                for (Client c: ride.getClients()) {
                    this.simpMessagingTemplate.convertAndSend(
                        "/client/reservation/"+c.getUsername(),
                        "15 minutes until your reservation."
                    );
                }
            } else if (
                now.isAfter(before10min.minus(30, ChronoUnit.SECONDS)) &&
                    now.isBefore(before10min.plus(30, ChronoUnit.SECONDS))
            ) {
                for (Client c: ride.getClients()) {
                    this.simpMessagingTemplate.convertAndSend(
                        "/client/reservation/"+c.getUsername(),
                        "10 minutes until your reservation."
                    );
                }
            } else if (
                now.isAfter(before5min.minus(30, ChronoUnit.SECONDS)) &&
                    now.isBefore(before5min.plus(30, ChronoUnit.SECONDS))
            ) {
                for (Client c: ride.getClients()) {
                    this.simpMessagingTemplate.convertAndSend(
                        "/client/reservation/"+c.getUsername(),
                        "5 minutes until your reservation."
                    );
                }
            } else if (
                now.isAfter(reserveTime.minus(30, ChronoUnit.SECONDS))
            ) {
                for (Client c: ride.getClients()) {
                    this.simpMessagingTemplate.convertAndSend(
                        "/client/reservation/"+c.getUsername(),
                        "Your reserved ride will arrive shortly."
                    );
                }
                Optional<List<Driver>> drivers = driverRepository.findNearestFreeDriver();
                if (drivers.isPresent() && !drivers.get().isEmpty()) {
                    Driver driver = drivers.get().get(0);
                    driver.setStatus(DriverStatus.PENDING);
                    driverRepository.save(driver);
                    ride.setStatus(RideStatus.ON_ROUTE);
                    ride.setDriver(driver);
                    rideRepository.save(ride);
                    this.simpMessagingTemplate.convertAndSend(
                        "/driver/active-ride/"+driver.getUsername(),
                        new ActiveRideDto(ride)
                    );
                } else {
                    List<Ride> ridesNearestToEnd = getRidesNearestToEnd();
                    if (!ridesNearestToEnd.isEmpty()) {
                        Driver driver = ridesNearestToEnd.get(0).getDriver();
                        driver.setReserved(true);
                        driverRepository.save(driver);
                        ride.setStatus(RideStatus.WAITING);
                        ride.setDriver(driver);
                        rideRepository.save(ride);
                        this.simpMessagingTemplate.convertAndSend(
                            "/driver/ride-assigned/" + driver.getUsername(),
                            new ActiveRideDto(ride)
                        );
                    }
                }

            }
        }
    }

    public ActiveRideDto reserveRide(RideInfoDto rideInfoDto) {
        Set<Client> clients = getClients(rideInfoDto);
        Set<Address> route = getRoute(rideInfoDto);

        Ride ride = new Ride(null, route, rideInfoDto.getPrice(), rideInfoDto.getDistance());
        clients.forEach(ride::addClient);

        this.rideRepository.save(ride);
        clientRepository.saveAll(clients);
        return new ActiveRideDto(ride);
    }

    public Ride getById(long id) {
        return rideRepository.getById(id);
    }
}
