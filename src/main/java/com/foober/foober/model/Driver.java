package com.foober.foober.model;

import com.foober.foober.model.enumeration.DriverStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "Driver")
@Table(name = "drivers")
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
}