package com.foober.foober.model;

import com.foober.foober.model.enumeration.RideStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Ride {
    private UUID Id;
    private List<Address> route;
    private Set<Client> clients;
    private double price;
    private double distance;
    private RideStatus status;
    private Driver driver;
    private Long startTime;
    private Long endTime;


}