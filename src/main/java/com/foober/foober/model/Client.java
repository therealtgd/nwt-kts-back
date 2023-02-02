package com.foober.foober.model;

import javax.persistence.*;

import com.foober.foober.model.enumeration.ClientStatus;
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
    @ManyToMany
    @JoinTable(
            name = "client_ride_favorite",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"))
    private Set<Ride> favorites = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    public Client(String username,
                  String email,
                  String password,
                  String displayName,
                  String phoneNumber,
                  String city,
                  Set<Role> authorities,
                  String paymentInfo,
                  String provider,
                  String providerUserId) {
        this.enabled = true;
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.authorities = authorities;
        this.isActivated = false;
        this.paymentInfo = paymentInfo;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.status = ClientStatus.OFFLINE;
    }
}