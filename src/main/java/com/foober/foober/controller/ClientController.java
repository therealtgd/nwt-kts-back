package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.ClientSignUpConfirmation;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/client",
        produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/register/confirm")
    public ApiResponse<Object> confirmRegistration(@Valid @RequestBody ClientSignUpConfirmation data) {
        clientService.confirmRegistration(data.getToken());
        return new ApiResponse<>("Registration confirmed.");
    }

    @GetMapping("/credits")
    public ApiResponse<Integer> getCreditsBalance(@CurrentUser LocalUser user) {
        try {
            return new ApiResponse<>(clientService.getCreditsBalance(user.getUser()));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "The client doesn't exist.");
        }
    }

}
