package com.foober.foober.dto.ride;


import com.foober.foober.model.enumeration.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RideInfoDto {
    private double distance;
    private double duration;
    private AddressDto startAddress;
    private AddressDto endAddress;
    private VehicleType vehicleType;
    private SimpleDriverDto driver;
    private List<AddressDto> stops;
    private double price;
    private List<String> clients;

    public double getPrice() {
        return price * clients.size();
    }
}
