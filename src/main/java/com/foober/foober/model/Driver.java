package com.foober.foober.model;

import com.foober.foober.model.enumeration.DriverStatus;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Driver")
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
}