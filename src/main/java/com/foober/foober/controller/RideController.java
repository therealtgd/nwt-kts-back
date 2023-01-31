package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.dto.ApiResponse;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.service.DriverService;
import com.foober.foober.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/ride" ,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RideController {

    private final RideService rideService;
    private final DriverService driverService;

    @GetMapping("/price")
    public ApiResponse<Integer> getPrice(
        @RequestParam(value="vehicleType") String vehicleType,
        @RequestParam(value = "distance") int distance
    ) {
        try {
            return new ApiResponse<>(rideService.getPrice(vehicleType, distance));
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<?> acceptRide(@CurrentUser LocalUser user, @PathVariable("id") String id) {
        this.driverService.updateStatus(user.getUser().getId(), DriverStatus.BUSY);
        // TODO: Set ride status to ON_ROUTE
        return new ApiResponse<>(HttpStatus.OK);
    }

    @Transactional
    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<?> endRide(@CurrentUser LocalUser user, @PathVariable("id") String id) {
        this.driverService.updateStatus(user.getUser().getId(), DriverStatus.AVAILABLE);
        // TODO: Set ride status to FINISHED
        return new ApiResponse<>(HttpStatus.OK);
    }

    @GetMapping("/nearest-free-driver")
    public ApiResponse<SimpleDriverDto> getNearestFreeDriver(
            @RequestParam(value="vehicleType") String vehicleType,
            @RequestParam(value="petsAllowed") boolean petsAllowed,
            @RequestParam(value="babiesAllowed") boolean babiesAllowed,
            @RequestParam(value="lat") double lat,
            @RequestParam(value="lng") double lng
    ) {
        return new ApiResponse<>(driverService.getNearestFreeDriver(vehicleType, petsAllowed, babiesAllowed, lat, lng));
    }

    @PostMapping("/order")
    public ApiResponse<Object> orderRide(@RequestBody RideInfoDto rideInfoDto) {
        this.rideService.orderRide(rideInfoDto);
        return new ApiResponse<>(HttpStatus.OK);
    }

}
