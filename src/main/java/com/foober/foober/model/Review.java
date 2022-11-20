package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private int ID;
    private int rating;
    private Driver driver;
    private Client client;
    private Long timeStamp;
}