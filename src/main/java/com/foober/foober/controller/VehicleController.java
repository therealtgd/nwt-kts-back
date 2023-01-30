package com.foober.foober.controller;

import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.LatLng;
import com.foober.foober.dto.VehicleDto;
import com.foober.foober.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    path = "/vehicle",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PutMapping(path = "/update/{id}/position")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<LatLng> updateVehicleLocation(@PathVariable("id") Long id, @RequestBody LatLng latlng) {
        this.vehicleService.updateVehicleLocation(id, latlng);
        this.simpMessagingTemplate.convertAndSend(
            "/map-updates/update-vehicle-position",
            new VehicleDto(id, latlng)
        );
        return new ApiResponse<>(latlng);
    }

    @GetMapping(path = "/get-all")
    public ApiResponse<List<VehicleDto>> getAllVehicles() {
        return new ApiResponse<>(this.vehicleService.getAllVehicleDtos());
    }
}
