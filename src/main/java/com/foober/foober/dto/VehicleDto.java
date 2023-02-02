package com.foober.foober.dto;

import com.foober.foober.model.Vehicle;
import lombok.Data;

@Data
public class VehicleDto {
    private Long id;
    private String licencePlate;
    private int capacity;
    private boolean petsAllowed;
    private boolean babiesAllowed;
    private String vehicleType;
    private LatLng position;

    public VehicleDto(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.licencePlate = vehicle.getLicencePlate();
        this.capacity = vehicle.getCapacity();
        this.petsAllowed = vehicle.isPetsAllowed();
        this.babiesAllowed = vehicle.isBabiesAllowed();
        this.vehicleType = vehicle.getType().toString();
        this.position = new LatLng(vehicle.getLatitude(), vehicle.getLongitude());
    }

    public VehicleDto(Long id, LatLng latLng) {
        this.id = id;
        this.position = latLng;
    }
}
