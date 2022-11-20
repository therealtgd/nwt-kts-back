package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PendingDriverChanges {
    private UUID ID;
    private String firstName;
    private String lastName;
    private String image;
    private Vehicle vehicle;
    private Long timeStamp;
}