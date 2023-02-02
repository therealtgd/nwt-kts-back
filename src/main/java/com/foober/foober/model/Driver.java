package com.foober.foober.model;

import com.foober.foober.model.enumeration.DriverStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Driver")
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private Set<Ride> rides = new HashSet<>();


}
