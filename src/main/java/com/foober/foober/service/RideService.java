package com.foober.foober.service;

import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.model.Address;
import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.Ride;
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

import java.util.*;

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

    public void orderRide(RideInfoDto rideInfoDto) {
        Driver driver = driverRepository.getById(rideInfoDto.getDriver().getId());
        Set<Client> clients = clientRepository.findUsersByUsernames(rideInfoDto.getClients());
        Set<Address> route = new HashSet<>();
        route.add(new Address(rideInfoDto.getStartAddress(), 0));
        route.addAll(rideInfoDto.getStops().stream()
                .map((a) -> new Address(a, route.size()))
                .toList()
        );
        route.add(new Address(rideInfoDto.getEndAddress(), route.size()));
        Ride ride = new Ride(driver, clients, route, rideInfoDto.getPrice(), rideInfoDto.getDistance());
        rideRepository.save(ride);
        // TODO: Send notification to Driver
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
            .units(Unit.METRIC)
            .await();
        DistanceMatrix matrix = request.await();
        return matrix.rows[0].elements[0].duration.inSeconds;
    }
}
