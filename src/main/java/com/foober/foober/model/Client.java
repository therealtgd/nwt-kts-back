package com.foober.foober.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client extends User {

    @Column(name="phone_number", columnDefinition = "TEXT")
    private String phoneNumber;
    @Column(name="is_activated", nullable = false)
    private boolean isActivated;
    @Column(name="payment_info", nullable = false, columnDefinition = "TEXT")
    private String paymentInfo;
    @ManyToMany
    @JoinTable(
            name = "client_ride",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"))
    private Set<Ride> rides = new HashSet<>();

    public Client(String username,
                  String email,
                  String password,
                  String displayName,
                  Set<Role> authorities,
                  String paymentInfo,
                  String provider,
                  String providerUserId) {
        this.enabled = true;
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.authorities = authorities;
        this.isActivated = false;
        this.paymentInfo = paymentInfo;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }
}