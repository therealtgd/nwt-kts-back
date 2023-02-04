package com.foober.foober.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PendingDriverChanges")
public class PendingDriverChanges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    @Column(name = "display_name", nullable = false, columnDefinition = "TEXT")
    private String displayName;
    @Column(name = "phone_number", nullable = false, columnDefinition = "TEXT")
    private String phoneNumber;
    @Column(name = "username", nullable = false, columnDefinition = "TEXT")
    private String username;
    @Column(name = "city", nullable = false, columnDefinition = "TEXT")
    private String city;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image profilePicture;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
    @Column(nullable = false)
    private Long timeStamp;

    public PendingDriverChanges(Driver driver,
                                String displayName,
                                String username,
                                String phoneNumber,
                                String city,
                                Image profilePicture,
                                Vehicle vehicle,
                                Long timeStamp) {
        this.driver = driver;
        this.displayName = displayName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.profilePicture = profilePicture;
        this.vehicle = vehicle;
        this.timeStamp = timeStamp;
    }

    public PendingDriverChanges(Driver driver,
                                String displayName,
                                String username,
                                String phoneNumber,
                                String city,
                                Vehicle vehicle,
                                Long timeStamp) {
        this.driver = driver;
        this.displayName = displayName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.vehicle = vehicle;
        this.timeStamp = timeStamp;
    }
}