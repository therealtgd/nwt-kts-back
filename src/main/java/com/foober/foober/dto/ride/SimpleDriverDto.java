package com.foober.foober.dto.ride;

import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleDriverDto {
    private long id;
    private String displayName;
    private DriverStatus status;

    public SimpleDriverDto(Driver driver) {
        this.id = driver.getId();
        this.displayName = driver.getDisplayName();
        this.status = driver.getStatus();
    }
}
