package com.foober.foober.model;

import com.foober.foober.dto.ReviewDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int driverRating;
    @Column(nullable = false)
    private int vehicleRating;
    @Column(nullable = false)
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(nullable = false)
    private Long timeStamp;

    public Review(ReviewDto reviewDto, Ride ride, Client client) {
        this.driverRating = reviewDto.getDriverRating();
        this.vehicleRating = reviewDto.getVehicleRating();
        this.comment = reviewDto.getComment();
        this.ride = ride;
        this.client = client;
        this.timeStamp = System.currentTimeMillis();
    }

    public Review(int driverRating, int vehicleRating, String comment, Ride ride, Client client, Long timeStamp) {
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.ride = ride;
        this.client = client;
        this.timeStamp = timeStamp;
    }
}