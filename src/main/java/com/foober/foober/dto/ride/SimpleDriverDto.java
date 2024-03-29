package com.foober.foober.dto.ride;

import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleDriverDto {
    private long id;
    private String username;
    private String displayName;
    private DriverStatus status;
    private boolean isReserved;

    public SimpleDriverDto(Driver driver) {
        this.id = driver.getId();
        this.username = driver.getUsername();
        this.displayName = driver.getDisplayName();
        this.status = driver.getStatus();
        this.isReserved = driver.isReserved();
    }
}
