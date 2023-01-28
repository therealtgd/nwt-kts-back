package com.foober.foober.controller;

import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.ClientSignUpRequest;
import com.foober.foober.dto.ClientSignUpConfirmation;
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

    @PostMapping("/register/confirm")
    public ApiResponse confirmRegistration(@Valid @RequestBody ClientSignUpConfirmation data) {
        clientService.confirmRegistration(data.getToken());
        return new ApiResponse(true, "Registration confirmed.");
    }
}
