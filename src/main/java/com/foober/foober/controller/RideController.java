package com.foober.foober.controller;

import com.foober.foober.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ride" ,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping("/price")
    public ResponseEntity<Integer> getPrice(
        @RequestParam(value="vehicleType") String vehicleType,
        @RequestParam(value = "distance") int distance
    ) {
        try {
            return ResponseEntity.ok(rideService.getPrice(vehicleType, distance));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
