package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.dto.*;
import com.foober.foober.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(
    value = "/driver"
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
    @GetMapping(path = "/me")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<DriverDto> getActiveDrivers(@CurrentUser LocalUser user) {
        return new ApiResponse<>(driverService.getMe(user.getUser()));
    }
    @PostMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<?> update(@CurrentUser LocalUser user, @Valid @RequestPart("updateRequest") DriverUpdateRequest updateRequest, @RequestPart(value = "image", required=false) MultipartFile image) {
        try {
            driverService.update(user.getUser(), updateRequest, image);
            return new ApiResponse<>("Successfully sent the update request.");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Could not upload the file: %s!",
                    image.getOriginalFilename()));
        }
    }
}
