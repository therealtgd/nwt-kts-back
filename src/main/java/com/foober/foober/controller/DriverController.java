package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.DriverDto;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        return new ApiResponse<>(this.driverService.getDriverDto(user.getUser().getId()));
    }

    @GetMapping(path = "/get-all-active")
    public ApiResponse<List<DriverDto>> getActiveDrivers() {
        return new ApiResponse<>(this.driverService.getActiveDriverDtos());
    }
}
