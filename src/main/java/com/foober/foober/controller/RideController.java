package com.foober.foober.controller;

import com.foober.foober.config.CurrentUser;
import com.foober.foober.dto.*;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.service.DriverService;
import com.foober.foober.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/ride" ,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RideController {

    private final RideService rideService;
    private final DriverService driverService;
    private final SimpMessagingTemplate simpMessagingTemplate;

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

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<Long> startRide(@CurrentUser LocalUser user, @PathVariable("id") long id) {
        this.driverService.updateStatus(user.getUser().getId(), DriverStatus.BUSY);
        long startTime = this.rideService.startRide(id);
        this.simpMessagingTemplate.convertAndSend(
            "/driver/ride-started/"+user.getUser().getUsername(),
            this.driverService.getCurrentRideByDriver((Driver) user.getUser())
        );
        return new ApiResponse<>(startTime);
    }

    @Transactional
    @PutMapping("/{id}/finish")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<?> finishRide(@CurrentUser LocalUser user, @PathVariable("id") long id) {
        long ms = this.rideService.finishRide(id);
        ActiveRideDto ride = this.driverService.getNextRide((Driver) user.getUser());
        this.simpMessagingTemplate.convertAndSend(
                "/client/ride-finished/"+user.getUser().getUsername(),
                "You arrived at the destination."
        );
        if (ride != null) {
            this.simpMessagingTemplate.convertAndSend(
                    "/driver/active-ride/"+ride.getDriver().getUsername(),
                    ride
            );
        } else {
            this.driverService.updateStatus(user.getUser().getId(), DriverStatus.AVAILABLE);
        }
        return new ApiResponse<>(ms);
    }

    @Transactional
    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<?> endRide(@CurrentUser LocalUser user,
                                  @PathVariable("id") long id,
                                  @RequestBody RideCancellationDto rideCancellationDto
    ) {
        this.driverService.updateStatus(user.getUser().getId(), DriverStatus.AVAILABLE);
        this.rideService.endRide(id, rideCancellationDto);
        for (Client c : rideService.getRideClients(id))
            this.simpMessagingTemplate.convertAndSend(
                    "/client/ride-cancelled/"+c.getUsername(),
                    "The driver cancelled your ride.\nYour tokens have been refunded."
            );
        return new ApiResponse<>(HttpStatus.OK);
    }

    @GetMapping("/nearest-free-driver")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<SimpleDriverDto> getNearestFreeDriver(
            @RequestParam(value="vehicleType") String vehicleType,
            @RequestParam(value="petsAllowed") boolean petsAllowed,
            @RequestParam(value="babiesAllowed") boolean babiesAllowed,
            @RequestParam(value="lat") double lat,
            @RequestParam(value="lng") double lng
    ) {
        SimpleDriverDto simpleDriverDto = driverService.getNearestFreeDriver(vehicleType, petsAllowed, babiesAllowed, lat, lng);
        this.simpMessagingTemplate.convertAndSend(
                "/driver/reserved/"+simpleDriverDto.getUsername(),
                "You have been reserved for a ride."
        );
        return new ApiResponse<>(simpleDriverDto);
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<ActiveRideDto> orderRide(@RequestBody RideInfoDto rideInfoDto, @CurrentUser LocalUser user) {
        ActiveRideDto ride = this.rideService.orderRide(rideInfoDto, (Client) user.getUser());


        return new ApiResponse<>(ride);
    }

    @GetMapping("/report/client/{start}/{end}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<ReportDto> getClientReport(@CurrentUser LocalUser user, @PathVariable String start, @PathVariable String end) {
        return new ApiResponse<>(rideService.getClientReport((Client) user.getUser(), start, end));
    }

    @GetMapping("/report/driver/{start}/{end}")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    public ApiResponse<ReportDto> getDriverReport(@CurrentUser LocalUser user, @PathVariable String start, @PathVariable String end) {
        return new ApiResponse<>(rideService.getDriverReport((Driver) user.getUser(), start, end));
    }

    @GetMapping("/report/admin/{start}/{end}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<ReportDto> getAdminReport(@CurrentUser LocalUser user, @PathVariable String start, @PathVariable String end) {
        return new ApiResponse<>(rideService.getAdminReport(start, end));
    }

    @GetMapping("/driver-eta")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<Long> getDriverEta(@CurrentUser LocalUser user) {
        return new ApiResponse<>(rideService.getDriverEta(user.getUser()));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<?> postReview(@CurrentUser LocalUser user, @PathVariable long id, @RequestBody ReviewDto reviewDto) {
        rideService.reviewRide(id, reviewDto, user.getUser());
        return new ApiResponse<>(HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse<DetailedRideDto> getDriverEta(@CurrentUser LocalUser user, @PathVariable Long id) {
        return new ApiResponse<>(rideService.getRide(user.getUser(), id));
    }

    @PutMapping("/{id}/accept-split-fare")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<?> acceptSplitFare(@PathVariable long id) {
        rideService.acceptSplitFare(id);
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/decline-split-fare")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ApiResponse<?> declineSplitFare(@PathVariable long id) {
        Driver driver = rideService.declineSplitFare(id);
        ActiveRideDto ride = this.driverService.getNextRide(driver);
        if (ride != null) {
            this.simpMessagingTemplate.convertAndSend(
                    "/driver/active-ride/"+ride.getDriver().getUsername(),
                    ride
            );
        } else {
            this.driverService.updateStatus(driver.getId(), DriverStatus.AVAILABLE);
        }
        return new ApiResponse<>(HttpStatus.OK);
    }

}
