package com.foober.foober.model;

import com.foober.foober.dto.ride.AddressDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private int station;
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String streetAddress;

    public Address(int station, double latitude, double longitude, String streetAddress) {
        this.station = station;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
    }

    public Address(AddressDto addressDto, int station) {
        this.latitude = addressDto.getCoordinates().getLat();
        this.longitude = addressDto.getCoordinates().getLng();
        this.streetAddress = addressDto.getAddress();
        this.station = station;
    }

}