package com.foober.foober.model;

import com.foober.foober.model.enumeration.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vehicle {
    public int ID;
    private String registrationPlate;
    private int capacity;
    private boolean petsAllowed;
    private boolean babiesAllowed;
    private VehicleType type;
}