package com.foober.foober.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "Address")
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;
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