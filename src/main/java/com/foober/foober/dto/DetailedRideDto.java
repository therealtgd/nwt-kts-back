package com.foober.foober.dto;

import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.model.Ride;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.foober.foober.util.DtoConverter.longToTime;

@Data
@Getter
@Setter
public class DetailedRideDto {
    private UserBriefDisplay driver;
    private List<UserBriefDisplay> clients;
    private double distance;
    private double duration;
    private List<AddressDto> stops;
    private List<FullReviewDto> reviews;
    private double price;
    private double vehicleRating;
    private double driverRating;
    private String startTime;
    private String endTime;

    public DetailedRideDto(Ride ride) {
        this.distance = ride.getDistance();
        this.duration = ride.getEta();
        this.driver = new UserBriefDisplay(ride.getDriver());
        this.price = ride.getPrice();
        this.clients = ride.getClients().stream().map(UserBriefDisplay::new).toList();
        this.startTime = longToTime(ride.getStartTime());
        this.endTime = longToTime(ride.getEndTime());
    }
}
