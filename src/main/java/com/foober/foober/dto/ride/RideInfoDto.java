package com.foober.foober.dto.ride;


import com.foober.foober.model.Ride;
import com.foober.foober.model.User;
import com.foober.foober.model.enumeration.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.NoSuchElementException;

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

    public RideInfoDto(Ride ride) {
        this.distance = ride.getDistance();
        this.duration = ride.getEta();
        this.startAddress = new AddressDto(ride.getRoute().stream()
                .filter(a -> a.getStation() == 0)
                .findFirst().orElseThrow(NoSuchElementException::new));
        this.endAddress = new AddressDto(ride.getRoute().stream()
                .filter(a -> a.getStation() == ride.getRoute().size() - 1)
                .findFirst().orElseThrow(NoSuchElementException::new));
        this.stops = ride.getRoute().stream()
                .filter(a -> a.getStation() != 0 && a.getStation() != ride.getRoute().size() - 1)
                .map(AddressDto::new).toList();
        this.vehicleType = ride.getDriver().getVehicle().getType();
        this.driver = new SimpleDriverDto(ride.getDriver());
        this.price = ride.getPrice();
        this.clients = ride.getClients().stream().map(User::getUsername).toList();
    }
}
