package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Review {
    private UUID ID;
    private int rating;
    private Driver driver;
    private Client client;
    private Long timeStamp;
}