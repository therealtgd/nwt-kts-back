package com.foober.foober.model;

import com.foober.foober.model.enumeration.DriverStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Driver extends User {
    private DriverStatus status;
    private Vehicle vehicle;
}
