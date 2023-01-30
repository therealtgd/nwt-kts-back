package com.foober.foober.service;

import com.foober.foober.dto.DriverDto;
import com.foober.foober.model.Driver;
import com.foober.foober.repos.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
