package com.foober.foober.service;

import com.foober.foober.dto.DriverDto;
import com.foober.foober.dto.RideBriefDisplay;
import com.foober.foober.model.Driver;
import com.foober.foober.model.User;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.repos.DriverRepository;
import com.foober.foober.util.DtoConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;

    public List<DriverDto> getActiveDriverDtos() {
        List<Driver> drivers = this.driverRepository.findAllActive();
        List<DriverDto> driverDtos = new ArrayList<>();
        for(Driver driver : drivers) {
            driverDtos.add(new DriverDto(driver));
        }
        return driverDtos;
    }

    public DriverDto getDriverDto(Long id) {
        return new DriverDto(this.driverRepository.getById(id));
    }

    public Set<RideBriefDisplay> getRides(User user) {
        Driver driver = (Driver) user;
        Set<RideBriefDisplay> rides = new HashSet<>();
        driver.getRides().stream().filter(ride -> ride.getStatus() == RideStatus.COMPLETED).forEach(ride -> rides.add(DtoConverter.rideToBriefDisplay(ride)));
        return rides;
    }
}
