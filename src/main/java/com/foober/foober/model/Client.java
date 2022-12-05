package com.foober.foober.model;

import com.foober.foober.model.enumeration.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Client")
@Table(name = "clients")
public class Client extends User {

    @Column(name = "city", nullable = false, columnDefinition = "TEXT")
    private String city;
    @Column(name="phone_number", nullable = false, columnDefinition = "TEXT")
    private String phoneNumber;
    @Column(name="is_activated", nullable = false)
    private boolean isActivated;
    @Column(name="payment_info", nullable = false, columnDefinition = "TEXT")
    private String paymentInfo;

    public Client(String username,
                  String email,
                  String password,
                  String firstName,
                  String lastName,
                  Role authority,
                  String image,
                  String city,
                  String phoneNumber,
                  boolean isActivated,
                  String paymentInfo) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authority = authority;
        this.image = image;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.isActivated = isActivated;
        this.paymentInfo = paymentInfo;
    }
}