package com.foober.foober.controller;

import com.foober.foober.dto.ClientRegistration;
import com.foober.foober.exception.*;
import com.foober.foober.service.ClientService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.IOException;


@RestController
@RequestMapping(value = "/client",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    @Autowired
    ClientService clientService;

    @PostMapping(value = "/register")
    @ExceptionHandler({EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class,
            ResourceAccessException.class, EmailNotSentException.class})
    public ResponseEntity<String> registerClient(@Valid @RequestBody ClientRegistration registrationData) {
        clientService.registerClient(registrationData);
        return ResponseEntity.ok("Registration email sent");
    }
}
