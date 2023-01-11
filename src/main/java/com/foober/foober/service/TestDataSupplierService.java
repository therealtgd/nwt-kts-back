package com.foober.foober.service;

import com.foober.foober.model.Admin;
import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.Role;
import com.foober.foober.repos.RoleRepository;
import com.foober.foober.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDataSupplierService implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        admin.setFirstName("Ana");
        admin.setLastName("Andjelic");
        admin.setAuthority(roleRepository.findByName("ROLE_ADMIN").get());

        Driver driver = new Driver();
        driver.setEnabled(true);
        driver.setUsername("driver");
        driver.setEmail("driver@gmail.com");
        driver.setPassword(passwordEncoder.encode("driver"));
        driver.setFirstName("Nikola");
        driver.setLastName("Damjanovic");
        driver.setAuthority(roleRepository.findByName("ROLE_DRIVER").get());

        Client client = new Client();
        client.setEnabled(true);
        client.setUsername("client");
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setFirstName("Vladan");
        client.setLastName("Mikic");
        client.setAuthority(roleRepository.findByName("ROLE_CLIENT").get());
    }

    private void initializeRoles() {
        roleRepository.save(new Role("ROLE_ADMIN"));
        roleRepository.save(new Role("ROLE_DRIVER"));
        roleRepository.save(new Role("ROLE_CLIENT"));
    }
}
