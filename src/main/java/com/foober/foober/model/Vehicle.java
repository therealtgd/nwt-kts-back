package com.foober.foober.model;

import com.foober.foober.model.enumeration.VehicleType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Vehicle {
    private UUID Id;
    private String licencePlate;
    private int capacity;
    private boolean petsAllowed;
    private boolean babiesAllowed;
    private VehicleType type;
}