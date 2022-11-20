package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Review {
    private UUID Id;
    private int rating;
    private Ride ride;
    private Client client;
    private Long timeStamp;
}