package com.foober.foober.service;

import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.RoleRepository;
import com.foober.foober.repos.UserRepository;
import com.foober.foober.repos.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class TestDataSupplierService implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUsers();
    }

    private void initializeUsers() {
        Admin admin = new Admin();
        admin.setEnabled(true);
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setDisplayName("Ana Andjelic");
        admin.setAuthorities(Set.of(roleRepository.findByName("ROLE_ADMIN"), roleRepository.findByName("ROLE_USER")));
        userRepository.save(admin);

        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword(passwordEncoder.encode("driver"));
        driver.setDisplayName("Nikola Damjanovic");
        driver.setAuthorities(Set.of(roleRepository.findByName("ROLE_DRIVER"), roleRepository.findByName("ROLE_USER")));
        driver.setStatus(DriverStatus.AVAILABLE);
        Vehicle vehicle = new Vehicle("BEII END", 3, false, false, VehicleType.SEDAN);
        vehicle.setLatitude(45.24772);
        vehicle.setLongitude(19.836540000000003);
        vehicleRepository.save(vehicle);
        driver.setVehicle(vehicle);
        userRepository.save(driver);

        // test drivers
        {
            Driver test_driver_1 = new Driver();
            test_driver_1.setEnabled(true);
            test_driver_1.setUsername("testdriver1");
            test_driver_1.setEmail("driver1@gmail.com");
            test_driver_1.setPassword(passwordEncoder.encode("driver"));
            test_driver_1.setDisplayName("Test Driver");
            test_driver_1.setAuthorities(Set.of(roleRepository.findByName("ROLE_DRIVER"), roleRepository.findByName("ROLE_USER")));
            test_driver_1.setStatus(DriverStatus.AVAILABLE);
            Vehicle test_vehicle_1 = new Vehicle("SWAGGER", 5, false, true, VehicleType.SEDAN);
            test_vehicle_1.setLatitude(45.24146739121831);
            test_vehicle_1.setLongitude(19.831773947286283);
            test_driver_1.setVehicle(test_vehicle_1);
            vehicleRepository.save(test_vehicle_1);
            userRepository.save(test_driver_1);

            Driver test_driver_2 = new Driver();
            test_driver_2.setEnabled(true);
            test_driver_2.setUsername("testdriver2");
            test_driver_2.setEmail("driver2@gmail.com");
            test_driver_2.setPassword(passwordEncoder.encode("driver"));
            test_driver_2.setDisplayName("Test Driver");
            test_driver_2.setAuthorities(Set.of(roleRepository.findByName("ROLE_DRIVER"), roleRepository.findByName("ROLE_USER")));
            test_driver_2.setStatus(DriverStatus.AVAILABLE);
            Vehicle test_vehicle_2 = new Vehicle("SHAEK", 4, true, false, VehicleType.CARAVAN);
            test_vehicle_2.setLatitude(45.26476693594242);
            test_vehicle_2.setLongitude(19.83119512705716);
            test_driver_2.setVehicle(test_vehicle_2);
            vehicleRepository.save(test_vehicle_2);
            userRepository.save(test_driver_2);

            Driver test_driver_3 = new Driver();
            test_driver_3.setEnabled(true);
            test_driver_3.setUsername("testdriver3");
            test_driver_3.setEmail("driver3@gmail.com");
            test_driver_3.setPassword(passwordEncoder.encode("driver"));
            test_driver_3.setDisplayName("Test Driver");
            test_driver_3.setAuthorities(Set.of(roleRepository.findByName("ROLE_DRIVER"), roleRepository.findByName("ROLE_USER")));
            test_driver_3.setStatus(DriverStatus.AVAILABLE);
            Vehicle test_vehicle_3 = new Vehicle("FREEDOT", 4, true, true, VehicleType.SEDAN);
            test_vehicle_3.setLatitude(45.24615040400357);
            test_vehicle_3.setLongitude(19.849529457351448);
            test_driver_3.setVehicle(test_vehicle_3);
            vehicleRepository.save(test_vehicle_3);
            userRepository.save(test_driver_3);
        }

        Client client = new Client();
        client.setUsername("client");
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setDisplayName("Vladan Mikic");
        client.setAuthorities(Set.of(roleRepository.findByName("ROLE_CLIENT"), roleRepository.findByName("ROLE_USER")));
        client.setPaymentInfo("");
        client.setPhoneNumber("068419532");
        client.setCredits(10000000);
        client.setEnabled(true);
        userRepository.save(client);
    }

    private void initializeRoles() {
        roleRepository.save(new Role("ROLE_ADMIN"));
        roleRepository.save(new Role("ROLE_DRIVER"));
        roleRepository.save(new Role("ROLE_CLIENT"));
        roleRepository.save(new Role("ROLE_USER"));
    }
}
