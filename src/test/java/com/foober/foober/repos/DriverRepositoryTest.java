package com.foober.foober.repos;

import com.foober.foober.model.Driver;
import com.foober.foober.model.Vehicle;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @AfterEach
    void tearDown() {
        driverRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void findAllActive_StatusIsActive_ReturnsDriver() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findAllActive()).isEqualTo(List.of(driver));
    }

    @Test
    void findAllActive_StatusIsBusy_ReturnsDriver() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.BUSY);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findAllActive()).isEqualTo(List.of(driver));
    }

    @Test
    void findAllActive_StatusIsPending_ReturnsDriver() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.PENDING);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findAllActive()).isEqualTo(List.of(driver));
    }

    @Test
    void findAllActive_StatusIsOffline_ReturnsEmptyList() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.OFFLINE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findAllActive()).isEqualTo(new ArrayList<>());
    }

    @Test
    void findNearestFreeDriver_ShouldReturnEmptyList() {
        assertThat(driverRepository.findNearestFreeDriver(
                VehicleType.SEDAN,
                false,
                false,
                45.24772,
                19.836540000000003
        )).isEmpty();
    }

    @Test
    void findNearestFreeDriver_ShouldReturnDriver() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assert(driverRepository.findNearestFreeDriver(
                VehicleType.SEDAN,
                false,
                false,
                45.24772,
                19.836540000000003
        )).equals(Optional.of(List.of(driver)));
        assertThat(driverRepository.findNearestFreeDriver(
                VehicleType.SEDAN,
                false,
                false,
                45.24772,
                19.836540000000003
        )).isNotEmpty();
    }

    @Test
    void findNearestFreeDriver_WithWrongVehicleType_ShouldReturnEmptyList() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findNearestFreeDriver(
                VehicleType.WAGON,
                false,
                false,
                45.24772,
                19.836540000000003
        )).isEmpty();
    }

    @Test
    void findNearestFreeDriver_WithWrongPetsAllowed_ShouldReturnEmptyList() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findNearestFreeDriver(
                VehicleType.SEDAN,
                true,
                false,
                45.24772,
                19.836540000000003
        )).isEmpty();
    }

    @Test
    void findNearestFreeDriver_WithWrongBabiesAllowed_ShouldReturnEmptyList() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driver.setReserved(false);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assertThat(driverRepository.findNearestFreeDriver(
                VehicleType.SEDAN,
                false,
                true,
                45.24772,
                19.836540000000003
        )).isEmpty();
    }

    @Test
    void findAllByStatus_ShouldReturnDriver() {
        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword("password");
        driver.setDisplayName("Nikola Damjanovic");
        driver.setPhoneNumber("000000000");
        driver.setCity("Novi Sad");
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        driver.setVehicle(vehicle);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        assert(driverRepository.findAllByStatus(DriverStatus.AVAILABLE)).equals(List.of(driver));
    }

    @Test
    void findAllByStatus_ShouldReturnEmptyList() {
        assert(driverRepository.findAllByStatus(DriverStatus.AVAILABLE)).equals(new ArrayList<>());
    }
}