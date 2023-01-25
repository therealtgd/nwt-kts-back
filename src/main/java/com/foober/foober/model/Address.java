package com.foober.foober.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String streetAddress;

    public Address(double latitude, double longitude, String streetAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
    }
}