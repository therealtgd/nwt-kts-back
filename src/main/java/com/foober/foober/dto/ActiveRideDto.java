package com.foober.foober.dto;

import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.model.Ride;
import lombok.Data;

import java.util.List;
import java.util.NoSuchElementException;

@Data
public class ActiveRideDto {
    private DriverDto driver;
    private AddressDto startAddress;
    private AddressDto endAddress;
    private List<UserBriefDisplay> clients;
    private double distance;
    private double duration;
    private List<AddressDto> stops;
    private double price;
    private long eta;

    public ActiveRideDto(Ride ride) {
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
        this.driver = new DriverDto(ride.getDriver());
        this.price = ride.getPrice();
        this.clients = ride.getClients().stream().map(UserBriefDisplay::new).toList();
        this.eta = ride.getEta();
    }
}
