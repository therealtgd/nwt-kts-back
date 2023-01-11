package com.foober.foober.service;

import com.foober.foober.config.JwtUtil;
import com.foober.foober.dto.ClientRegistration;
import com.foober.foober.model.Client;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.repos.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil tokenUtils;
    public void registerClient(ClientRegistration registrationData) {
        Client newClient = new Client(registrationData.getUsername(),
                registrationData.getEmail(),
                registrationData.getPassword(),
                registrationData.getFirstName(),
                registrationData.getLastName(),
                roleRepository.findByName("ROLE_CLIENT").get(),
                // TODO: Replace with real image
                registrationData.getImage(),
                registrationData.getPhoneNumber(),
                false,
                // TODO: Add payment info
                "payment info"
        );
        clientRepository.save(newClient);
        String token = tokenUtils.generateConfirmationToken(newClient);
        emailService.sendRegistrationEmail(newClient, token);
    }
}
