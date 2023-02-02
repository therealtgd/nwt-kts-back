package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.DriverDto;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.RideBriefDisplay;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(
    value = "/driver",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public ApiResponse<DriverDto> getDriver(@CurrentUser LocalUser user) {
        return new ApiResponse<>(this.driverService.getCompatibleDriverDto(user.getUser().getId()));
    }
    @GetMapping(path = "/get-all-active")
    public ApiResponse<List<DriverDto>> getActiveDrivers() {
        return new ApiResponse<>(this.driverService.getActiveDriverDtos());
    }

    @GetMapping(path = "/get-all-by-status")
    public ApiResponse<List<DriverDto>> getAllByStatus(@RequestParam(name = "status") DriverStatus status, @CurrentUser LocalUser user) {
        return new ApiResponse<>(this.driverService.getAllByStatus(status, user != null ? user.getUser() : null));
    }

    @GetMapping("/rides")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<Set<RideBriefDisplay>> getRides(@CurrentUser LocalUser user) {
        return new ApiResponse<>(driverService.getRides(user.getUser()));
    }

    @Transactional
    @PutMapping("/unassign")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<?> unassignDriver(@RequestBody long id) {
        this.driverService.unassignDriver(id);
        return new ApiResponse<>("Successfully unassigned driver.");
    }
}
