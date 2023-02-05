package com.foober.foober.repos;

import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.model.enumeration.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class RideRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        Driver test_driver_3 = new Driver();
        test_driver_3.setEnabled(true);
        test_driver_3.setUsername("testdriver3");
        test_driver_3.setEmail("driver3@gmail.com");
        test_driver_3.setPassword("driver");
        test_driver_3.setDisplayName("Muhamed Hambdualahic");
        test_driver_3.setPhoneNumber("000000000");
        test_driver_3.setCity("Novi Sad");
        test_driver_3.setStatus(DriverStatus.AVAILABLE);
        Vehicle test_vehicle_3 = new Vehicle("FREEDOT", 4, true, true, VehicleType.SEDAN);
        test_vehicle_3.setLatitude(45.24615040400357);
        test_vehicle_3.setLongitude(19.849529457351448);
        test_driver_3.setVehicle(test_vehicle_3);
        vehicleRepository.save(test_vehicle_3);
        userRepository.save(test_driver_3);
        driverRepository.save(test_driver_3);

        Client client2 = new Client();
        client2.setUsername("client2");
        client2.setEmail("client2@gmail.com");
        client2.setPassword("client");
        client2.setDisplayName("Vladan Mikic");
        client2.setPhoneNumber("000000000");
        client2.setCity("Novi Sad");
        client2.setPaymentInfo("");
        client2.setPhoneNumber("068419532");
        client2.setCredits(10000000);
        client2.setEnabled(true);
        client2.setActivated(true);
        userRepository.save(client2);
        clientRepository.save(client2);
    }


    @Test
    void getAllInProgressNotReserved_ReturnsListOfRide() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.COMPLETED,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        ride3.setStatus(RideStatus.IN_PROGRESS);
        rideRepository.save(ride3);
        assertThat(rideRepository.getAllInProgressNotReserved()).isNotEmpty();
        assertThat(rideRepository.getAllInProgressNotReserved().get().get(0)).isEqualTo(ride3);
    }

    @Test
    void getAllInProgressNotReserved_ReturnsEmptyList() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.COMPLETED,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        rideRepository.save(ride3);
        assertThat(rideRepository.getAllInProgressNotReserved()).isEqualTo(Optional.of(new ArrayList<>()));
    }

    @Test
    void getAllInProgressNotReserved_DriverReserved_ReturnsEmptyList() {
        Driver driver = (Driver) userRepository.findByUsername("testdriver3").orElse(null);
        driver.setReserved(true);
        driverRepository.save(driver);
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.COMPLETED,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        rideRepository.save(ride3);
        assertThat(rideRepository.getAllInProgressNotReserved()).isEqualTo(Optional.of(new ArrayList<>()));
    }

    @Test
    void getActiveRideByClient_ReturnsClient() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.COMPLETED,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        ride3.setStatus(RideStatus.IN_PROGRESS);
        Client client = (Client) userRepository.findByUsername("client2").orElse(null);
        ride3.addClient(client);
        rideRepository.save(ride3);
        clientRepository.save(client);
        assertThat(rideRepository.getActiveRideByClient(client)).isNotEmpty();
        assertThat(rideRepository.getActiveRideByClient(client).get()).isEqualTo(ride3);
    }

    @Test
    void getActiveRideByClient_NoActiveRides_ReturnsEmpty() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.COMPLETED,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        Client client = (Client) userRepository.findByUsername("client2").orElse(null);
        ride3.addClient(client);
        rideRepository.save(ride3);
        clientRepository.save(client);
        assertThat(rideRepository.getActiveRideByClient(client)).isEmpty();
    }

    @Test
    void getActiveRideByClient_ReturnsClient2() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.ON_ROUTE,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        ride3.setStatus(RideStatus.IN_PROGRESS);
        Client client = (Client) userRepository.findByUsername("client2").orElse(null);
        ride3.addClient(client);
        rideRepository.save(ride3);
        clientRepository.save(client);
        assertThat(rideRepository.getActiveRideByClient(client)).isNotEmpty();
        assertThat(rideRepository.getActiveRideByClient(client).get()).isEqualTo(ride3);
    }

    @Test
    void getActiveRideByClient_ClientNotMember_ReturnsEmpty() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.ON_ROUTE,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        ride3.setStatus(RideStatus.IN_PROGRESS);
        Client client = (Client) userRepository.findByUsername("client2").orElse(null);
        rideRepository.save(ride3);
        assertThat(rideRepository.getActiveRideByClient(client)).isEmpty();
    }

    @Test
    void getInProgressRideByClientId_NotEmpty() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.ON_ROUTE,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        ride3.setStatus(RideStatus.IN_PROGRESS);
        rideRepository.save(ride3);
        User user = userRepository.findByUsername("testdriver3").orElseThrow(null);
        assertThat(rideRepository.getInProgressRideByClientId(user.getId())).isNotEmpty();
        assertThat(rideRepository.getInProgressRideByClientId(user.getId()).get()).isEqualTo(ride3);
    }

    @Test
    void getInProgressRideByClientId_NotEmpty2() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.IN_PROGRESS,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        ride3.setStatus(RideStatus.IN_PROGRESS);
        rideRepository.save(ride3);
        User user = userRepository.findByUsername("testdriver3").orElseThrow(null);
        assertThat(rideRepository.getInProgressRideByClientId(user.getId())).isNotEmpty();
        assertThat(rideRepository.getInProgressRideByClientId(user.getId()).get()).isEqualTo(ride3);
    }

    @Test
    void getInProgressRideByClientId_WrongRideStatus_ReturnsEmpty() {
        Ride ride3 = new Ride(
                Set.of(new Address(1, 45.24587607910168, 19.831609540763406, "Puskinova 3"),
                        new Address(2, 45.24553820777609, 19.83631330555981, "Gogoljeva 3")),
                479,
                2.4,
                RideStatus.COMPLETED,
                (Driver) userRepository.findByUsername("testdriver3").orElseThrow(),
                System.currentTimeMillis() - 2,
                System.currentTimeMillis()
        );
        rideRepository.save(ride3);
        User user = userRepository.findByUsername("testdriver3").orElseThrow(null);
        assertThat(rideRepository.getInProgressRideByClientId(user.getId())).isEmpty();
    }

    @Test
    void getInProgressRideByClientId_DriverNoActiveRides_ReturnsEmpty() {
        User user = userRepository.findByUsername("testdriver3").orElseThrow(null);
        assertThat(rideRepository.getInProgressRideByClientId(user.getId())).isEmpty();
    }
}