package com.foober.foober.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Client")
public class Client extends User {

    @Column(name="phone_number", columnDefinition = "TEXT")
    private String phoneNumber;
    @Column(name="is_activated", nullable = false)
    private boolean isActivated;
    @Column(name="payment_info", nullable = false, columnDefinition = "TEXT")
    private String paymentInfo;

    public Client(String username,
                  String email,
                  String password,
                  String displayName,
                  Set<Role> authorities,
                  String image,
                  String phoneNumber,
                  String paymentInfo,
                  String provider,
                  String providerUserId) {
        this.enabled = true;
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.authorities = authorities;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.isActivated = false;
        this.paymentInfo = paymentInfo;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }
}