package com.foober.foober.service;

import com.foober.foober.dto.LatLng;
import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DriverServiceTest {

    @InjectMocks
    private DriverService driverService;
    @Mock
    private  DriverRepository driverRepository;
    @Mock
    private  SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private  VehicleRepository vehicleRepository;
    @Mock
    private  RideRepository rideRepository;
    @Mock
    private  VehicleService vehicleService;
    @Mock
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    private final long DRIVER_ID = 1;

    @Test
    @DisplayName("Should set driver status to OFFLINE")
    public void testUpdateStatus_Success() {
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


        when(driverRepository.getById(Mockito.anyLong())).thenReturn(driver);
        driverService.updateStatus(this.DRIVER_ID, DriverStatus.OFFLINE);

        assertEquals(DriverStatus.OFFLINE, driver.getStatus());
    }

    @Test
    @DisplayName("Should fail when updating driver status - driver not found")
    public void testUpdateStatus_DriverNotFound() {
        assertThrows(RuntimeException.class, () -> driverService.updateStatus(this.DRIVER_ID, DriverStatus.BUSY));
    }

    @Test
    void sendDriversStatusAndPosition_shouldSendNotification() {
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

        when(driverRepository.findAllActive()).thenReturn(List.of(driver));
        verify(simpMessagingTemplate, Mockito.times(1));
    }

    @Test
    void sendDriversStatusAndPosition_shouldNotSendNotifications() {
        when(driverRepository.findAllActive()).thenReturn(new ArrayList<>());
        verify(simpMessagingTemplate, Mockito.times(0));
    }

    @Test
    @DisplayName("Should return active ride")
    void getActiveRide_ReturnsActiveRide() {
        Driver driver = Mockito.mock(Driver.class);
        Vehicle vehicle = Mockito.mock(Vehicle.class);
        when(driver.getVehicle()).thenReturn(vehicle);
        when(vehicle.getType()).thenReturn(VehicleType.SEDAN);
        driver.setVehicle(vehicle);
        Client client = Mockito.mock(Client.class);
        Address a1 = new Address(0, 45.24545101752263, 19.83228969045274, "Puskinova 1");
        Address a2 = new Address(1, 45.24575317126812, 19.835808748691015, "Gogoljeva 1");
        Ride ride = new Ride(
                Set.of(a1 ,a2),
                120,
                0.96,
                RideStatus.IN_PROGRESS,
               driver,
                System.currentTimeMillis() - 10000,
                System.currentTimeMillis()
        );
        ride.addClient(client);
        ride.setId(1L);

        when(rideRepository.getInProgressRideByClientId(Mockito.anyLong())).thenReturn(Optional.of(ride));

        assertThat(driverService.getActiveRide(driver).getDriver().getVehicle().getVehicleType()).isEqualTo(VehicleType.SEDAN.name());
    }

    @Test
    @DisplayName("Driver get active ride should return null")
    void getActiveRide_ShouldReturnNull() {
        assertThat(driverService.getActiveRide(Mockito.mock(Driver.class))).isEqualTo(null);
    }

    @Test
    @DisplayName("Simulate drive should update vehicle positions")
    void simulateDrive_ShouldUpdateVehiclePositions() {
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        LatLng latLng1 = new LatLng(46.222, 20.1112);
        LatLng latLng2 = new LatLng(47.222, 21.1112);
        vehicleRepository.save(vehicle);
        ArrayList<LatLng> list = new ArrayList<>();
        list.add(latLng1);
        list.add(latLng2);


        when(vehicleRepository.getById(Mockito.anyLong())).thenReturn(vehicle);
        when(vehicleRepository.save(Mockito.any(Vehicle.class))).thenReturn(vehicle);

        driverService.simulateDrive(vehicle, list);
        assertThat(vehicle.getLatitude()).isEqualTo(latLng2.getLat());
        assertThat(vehicle.getLongitude()).isEqualTo(latLng2.getLng());

    }

    @Test
    void simulateDrive_ShouldntUpdatePosition() {
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);

        ArrayList<LatLng> list = new ArrayList<>();

        driverService.simulateDrive(vehicle, list);
        assertThat(vehicle.getLatitude()).isEqualTo(45.24772);
        assertThat(vehicle.getLongitude()).isEqualTo(19.836540000000003);

    }

}
