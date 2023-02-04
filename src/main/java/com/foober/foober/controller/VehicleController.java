package com.foober.foober.controller;

import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.LatLng;
import com.foober.foober.dto.VehicleDto;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
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

    @PutMapping(path = "/update/{id}/position")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<LatLng> updateVehicleLocation(@PathVariable("id") Long id, @RequestBody LatLng latlng) {
        this.vehicleService.updateVehicleLocation(id, latlng);
        return new ApiResponse<>(latlng);
    }

    @GetMapping(path = "/get-all")
    public ApiResponse<List<VehicleDto>> getAllVehicles() {
        return new ApiResponse<>(this.vehicleService.getAllVehicleDtos());
    }

    @GetMapping(path = "/types")
    public ApiResponse<VehicleType[]> getAllVehicleTypes() {
        return new ApiResponse<>(this.vehicleService.getAllVehicleTypes());
    }
}
