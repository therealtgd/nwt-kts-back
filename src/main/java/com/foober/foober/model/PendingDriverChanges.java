package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingDriverChanges {
    private int ID;
    private String firstName;
    private String lastName;
    private String image;
    private Vehicle vehicle;
    private Long timeStamp;
}