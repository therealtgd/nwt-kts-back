package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Address {
    private UUID ID;
    private String latitude;
    private String longitude;
    private String streetAddress;
}