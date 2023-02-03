package com.foober.foober.dto;

import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.model.Ride;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class DetailedRideDto {
    private DriverDto driver;
    private List<UserBriefDisplay> clients;
    private double distance;
    private double duration;
    private List<AddressDto> stops;
    private List<ReviewDto> reviews;
    private double price;
    private double rating;

    public DetailedRideDto(Ride ride) {
        this.distance = ride.getDistance();
        this.duration = ride.getEta();
        this.stops = ride.getStops().stream().map(AddressDto::new).toList();
        this.driver = new DriverDto(ride.getDriver());
        this.price = ride.getPrice();
        this.clients = ride.getClients().stream().map(UserBriefDisplay::new).toList();
    }
}
