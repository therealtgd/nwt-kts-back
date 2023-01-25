package com.foober.foober.service;

import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.dto.ClientSignUpRequest;
import com.foober.foober.exception.*;
import com.foober.foober.model.Client;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.repos.RoleRepository;
import com.foober.foober.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.NoSuchElementException;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TokenProvider tokenUtils;

    public void confirmRegistration(String token) {

        try {
            tokenUtils.validateToken(token);
            Client client = clientRepository.findById(tokenUtils.getUserIdFromToken(token)).orElseThrow();
            if (client.isActivated()) {
                throw new UserAlreadyActivatedException("Client is already activated.");
            }
            client.setActivated(true);
            client.setEnabled(true);
            clientRepository.save(client);

        } catch (NoSuchElementException e) {
            throw new InvalidTokenException("Token is invalid");
        }

    }

}
