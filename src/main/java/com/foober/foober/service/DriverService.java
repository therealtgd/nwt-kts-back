package com.foober.foober.service;

import com.foober.foober.dto.DriverDto;
import com.foober.foober.dto.RideBriefDisplay;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.model.Driver;
import com.foober.foober.model.User;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.Ride;
import com.foober.foober.model.Vehicle;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.DriverRepository;
import com.foober.foober.util.DtoConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final RideService rideService;

    public List<DriverDto> getActiveDriverDtos() {
        List<Driver> drivers = this.driverRepository.findAllActive();
        List<DriverDto> driverDtos = new ArrayList<>();
        for(Driver driver : drivers) {
            driverDtos.add(new DriverDto(driver));
        }
        return driverDtos;
    }

    public DriverDto getCompatibleDriverDto(Long id) {
        return new DriverDto(this.driverRepository.getById(id));
    }

    public Set<RideBriefDisplay> getRides(User user) {
        Driver driver = (Driver) user;
        Set<RideBriefDisplay> rides = new HashSet<>();
        driver.getRides().stream().filter(ride -> ride.getStatus() == RideStatus.COMPLETED).forEach(ride -> rides.add(DtoConverter.rideToBriefDisplay(ride)));
        return rides;
    }
    
    @Transactional
    public void updateStatus(Long driver_id, DriverStatus status) {
        Driver driver = this.driverRepository.getById(driver_id);
        driver.setStatus(status);
        this.driverRepository.save(driver);
    }

    public SimpleDriverDto getNearestFreeDriver(
        String vehicleType,
        boolean petsAllowed,
        boolean babiesAllowed,
        double lat,
        double lng
    ) {
        Optional<List<Driver>> drivers = driverRepository.findNearestFreeDriver(
                Enum.valueOf(VehicleType.class, vehicleType), petsAllowed, babiesAllowed, lat, lng
        );
        SimpleDriverDto simpleDriverDto = null;
        if (drivers.isPresent() && !drivers.get().isEmpty()) {
            simpleDriverDto = new SimpleDriverDto(drivers.get().get(0));
        } else {
            List<Ride> rides = rideService.getRidesNearestToEnd();
            if (!rides.isEmpty()) {
                simpleDriverDto = getCompatibleDriverDto(vehicleType, petsAllowed, babiesAllowed, rides);
            }
        }
        return simpleDriverDto;
    }

    private static SimpleDriverDto getCompatibleDriverDto(String vehicleType, boolean petsAllowed, boolean babiesAllowed, List<Ride> rides) {
        for (Ride r : rides) {
            Vehicle vehicle = r.getDriver().getVehicle();
            if (vehicle.getType() == Enum.valueOf(VehicleType.class, vehicleType)
                && (!petsAllowed || vehicle.isPetsAllowed())
                && (!babiesAllowed || vehicle.isBabiesAllowed())
            ) {
                return new SimpleDriverDto(r.getDriver());
            }
        }
        return null;
    }
}

