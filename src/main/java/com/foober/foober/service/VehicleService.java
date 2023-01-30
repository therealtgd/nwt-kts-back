package com.foober.foober.service;

import com.foober.foober.dto.LatLng;
import com.foober.foober.dto.VehicleDto;
import com.foober.foober.exception.ResourceNotFoundException;
import com.foober.foober.model.Vehicle;
import com.foober.foober.repos.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public Vehicle findById(Long id) {
        return this.vehicleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle doesn't exist."));
    }

    public void updateVehicleLocation(Long id, LatLng latlng) throws ResourceNotFoundException {
        Vehicle vehicle = this.vehicleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle doesn't exist."));
        vehicle.setLatitude(latlng.getLat());
        vehicle.setLongitude(latlng.getLng());
        this.vehicleRepository.save(vehicle);
    }

    public List<VehicleDto> getAllVehicleDtos() {
        List<Vehicle> vehicles = this.vehicleRepository.findAll();
        List<VehicleDto> vehicleDtos = new ArrayList<>();
        for (Vehicle vehicle: vehicles) {
            vehicleDtos.add(new VehicleDto(vehicle));
        }
        return vehicleDtos;
    }
}
