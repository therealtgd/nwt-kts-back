package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PendingDriverChanges {
    private UUID Id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private Vehicle vehicle;
    private Long timeStamp;
}