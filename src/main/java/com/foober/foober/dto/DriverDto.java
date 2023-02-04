package com.foober.foober.dto;

import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;

import static com.foober.foober.util.GeneralUtils.TEMPLATE_IMAGE;

@Data
public class DriverDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private Boolean enabled;
    private DriverStatus status;
    private VehicleDto vehicle;
    private String image;

    public DriverDto(Driver driver) {
        this.id = driver.getId();
        this.username = driver.getUsername();
        this.email = driver.getEmail();
        this.displayName = driver.getDisplayName();
        this.enabled = driver.isEnabled();
        this.status = driver.getStatus();
        this.vehicle = new VehicleDto(driver.getVehicle());
        this.image = driver.getImage() != null ? Base64.encodeBase64String(driver.getImage().getData()) : TEMPLATE_IMAGE;
    }

}
