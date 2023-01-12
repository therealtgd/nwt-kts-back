package com.foober.foober.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.foober.foober.config.JwtUtil;
import com.foober.foober.dto.ClientRegistration;
import com.foober.foober.exception.*;
import com.foober.foober.model.Client;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.repos.RoleRepository;
import com.foober.foober.repos.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
public class ClientService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil tokenUtils;

    public void registerClient(ClientRegistration registrationData) {

        checkForExistingEmail(registrationData.getEmail());
        checkForExistingUsername(registrationData.getUsername());

        Client newClient = new Client(registrationData.getUsername(),
                registrationData.getEmail(),
                registrationData.getPassword(),
                registrationData.getFirstName(),
                registrationData.getLastName(),
                roleRepository.findByName("ROLE_CLIENT").get(),
                // TODO: Replace with real image
                registrationData.getImage(),
                registrationData.getPhoneNumber(),
                // TODO: Add payment info
                "payment info"
        );

        try {
            String token = tokenUtils.generateConfirmationToken(newClient);
            emailService.sendRegistrationEmail(newClient, token);
            clientRepository.save(newClient);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Email failed to send.");
        } catch (IOException e) {
            throw new ResourceNotFoundException("Email template was not found.");
        }

    }

    public void confirmRegistration(String token) throws ConfirmationLinkExpiredException, UserAlreadyActivatedException {

        try {
            DecodedJWT decodedJWT = tokenUtils.verifyToken(token);
            Client client = clientRepository.findByUsername(decodedJWT.getSubject()).orElseThrow();
            if (client.isActivated()) {
                throw new UserAlreadyActivatedException("Client is already activated.");
            }
            client.setActivated(true);
            client.setEnabled(true);
            clientRepository.save(client);

        } catch (TokenExpiredException e) {
            throw new ConfirmationLinkExpiredException("Confirmation link expired.");
        } catch (NoSuchElementException e) {
            throw new InvalidTokenException("InvalidTokenException");
        }

    }

    private void checkForExistingUsername(String username) {
        if (userRepository.findByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException("Username already exists.");
    }

    private void checkForExistingEmail(String email) {
        if (userRepository.findByEmail(email).isPresent())
            throw new EmailAlreadyExistsException("Email already exists.");
    }
}
