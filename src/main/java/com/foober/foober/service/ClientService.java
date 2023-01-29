package com.foober.foober.service;

import com.foober.foober.exception.InvalidTokenException;
import com.foober.foober.exception.UserAlreadyActivatedException;
import com.foober.foober.model.Client;
import com.foober.foober.model.User;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public int getCreditsBalance(User user) {
        return user.getCredits();
    }
}
