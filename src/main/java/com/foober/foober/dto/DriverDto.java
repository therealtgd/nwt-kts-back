package com.foober.foober.dto;

import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import lombok.Data;

@Data
public class DriverDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private Boolean enabled;
    private DriverStatus status;
    private VehicleDto vehicle;

    public DriverDto(Driver driver) {
        this.id = driver.getId();
        this.username = driver.getUsername();
        this.email = driver.getEmail();
        this.displayName = driver.getDisplayName();
        this.enabled = driver.isEnabled();
        this.status = driver.getStatus();
        this.vehicle = new VehicleDto(driver.getVehicle());
    }
}
