package com.foober.foober.service;

import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.RideRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RideService {

    private final int PRICE_PER_KM = 60;

    private final RideRepository rideRepository;

    public int getPrice(String vehicleType, int distance) throws IllegalArgumentException {
        return Enum.valueOf(VehicleType.class, vehicleType)
                .getPrice() + (distance / 1000) * PRICE_PER_KM;
    }
}
