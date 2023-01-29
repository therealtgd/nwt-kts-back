package com.foober.foober.service;

import com.foober.foober.model.Admin;
import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.Role;
import com.foober.foober.repos.RoleRepository;
import com.foober.foober.repos.UserRepository;
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
        userRepository.save(driver);

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
