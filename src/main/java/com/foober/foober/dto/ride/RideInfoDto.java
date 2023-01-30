package com.foober.foober.dto.ride;


import com.foober.foober.dto.DriverDto;
import com.foober.foober.model.enumeration.VehicleType;
import lombok.Data;

import java.util.List;

@Data
public class RideInfoDto {
    private TextAndValue distance;
    private TextAndValue duration;
    private Address startAddress;
    private Address endAddress;
    private VehicleType vehicleType;
    private DriverDto driver;
    private List<Address> stops;
    private double price;
    private List<String> clients;
}
