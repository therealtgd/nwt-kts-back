package com.foober.foober.model;

import com.foober.foober.model.enumeration.DriveStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Drive {
    private UUID ID;
    private List<Address> route;
    private Set<Client> clients;
    private double price;
    private double length;
    private DriveStatus status;
    private Driver driver;
    private Long startTime;
    private Long endTime;


}