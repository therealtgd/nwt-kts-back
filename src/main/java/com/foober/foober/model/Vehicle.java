package com.foober.foober.model;

import com.foober.foober.model.enumeration.VehicleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="licence_plate", nullable = false, columnDefinition = "TEXT")
    private String licencePlate;
    @Column(name="capacity", nullable = false)
    private int capacity;
    @Column(name="pets_allowed", nullable = false)
    private boolean petsAllowed;
    @Column(name="babies_allowed", nullable = false)
    private boolean babiesAllowed;
    @Column(name="latitude")
    private Double latitude;
    @Column(name="longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    public Vehicle(String licencePlate,
                   int capacity,
                   boolean petsAllowed,
                   boolean babiesAllowed,
                   VehicleType type) {
        this.licencePlate = licencePlate;
        this.capacity = capacity;
        this.petsAllowed = petsAllowed;
        this.babiesAllowed = babiesAllowed;
        this.type = type;
    }
}