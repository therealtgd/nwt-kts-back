package com.foober.foober.model;

import com.foober.foober.model.enumeration.DriverStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "Driver")
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
}