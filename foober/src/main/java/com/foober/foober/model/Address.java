package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private int ID;
    private String latitude;
    private String longitude;
    private String streetAddress;
}
