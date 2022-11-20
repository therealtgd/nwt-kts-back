package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client extends User {
    private String email;
    private String city;
    private String phoneNumber;
    private boolean isActivated;
    private String paymentInfo;
}