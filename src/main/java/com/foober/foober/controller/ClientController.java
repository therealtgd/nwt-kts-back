package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.*;
import com.foober.foober.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;


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
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<Integer> getCreditsBalance(@CurrentUser LocalUser user) {
        try {
            return new ApiResponse<>(clientService.getCreditsBalance(user.getUser()));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "The client doesn't exist.");
        }
    }
    @GetMapping("/rides")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<Set<RideBriefDisplay>> getRides(@CurrentUser LocalUser user) {
        return new ApiResponse<>(clientService.getRides(user.getUser()));
    }

    @GetMapping("/favorite-routes")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<List<RouteDto>> getFavoriteRoutes(@CurrentUser LocalUser user) {
        return new ApiResponse<>(clientService.getFavoriteRoutes(user.getUser()));
    }

    @PutMapping("/set-favorite/{rideId}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<?> setFavoriteRoute(@CurrentUser LocalUser user, @PathVariable long rideId) {
        clientService.addFavoriteRoute(user.getUser(), rideId);
        return new ApiResponse<>("Successfully added the route to favorites.");
    }

    @DeleteMapping("/remove-favorite/{rideId}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<?> deleteFavoriteRoute(@CurrentUser LocalUser user, @PathVariable long rideId) {
        clientService.removeFavoriteRoute(user.getUser(), rideId);
        return new ApiResponse<>("Successfully removed the route from favorites.");
    }
    
    @GetMapping("/active-ride")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<ActiveRideDto> getActiveRide(@CurrentUser LocalUser user) {
        return new ApiResponse<>(clientService.getActiveRide(user.getUser()));
    }

}
