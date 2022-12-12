package com.foober.foober.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "Review")
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;
    @Column(nullable = false)
    private int rating;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(nullable = false)
    private Long timeStamp;

    public Review(int rating,
                  Ride ride,
                  Client client,
                  Long timeStamp) {
        this.rating = rating;
        this.ride = ride;
        this.client = client;
        this.timeStamp = timeStamp;
    }
}