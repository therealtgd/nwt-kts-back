package com.foober.foober.model;

import com.foober.foober.model.enumeration.ClientStatus;
import com.foober.foober.model.enumeration.DriverStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Driver")
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private Set<Ride> rides = new HashSet<>();
    // Driver is near the end of their current ride and has a reservation for the next ride
    private boolean isReserved;

    public Driver(String username,
                  String email,
                  String password,
                  String displayName,
                  String phoneNumber,
                  String city,
                  Set<Role> authorities,
                  Vehicle vehicle) {
        this.enabled = true;
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.authorities = authorities;
        this.vehicle = vehicle;
        this.status = DriverStatus.OFFLINE;
    }
}
