package com.foober.foober.controller;

import com.foober.foober.dto.ClientRegistration;
import com.foober.foober.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/client",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    @Autowired
    ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<String>  registerClient(@Valid @RequestBody ClientRegistration registrationData) {
        clientService.registerClient(registrationData);
        return ResponseEntity.ok("");
    }
}
