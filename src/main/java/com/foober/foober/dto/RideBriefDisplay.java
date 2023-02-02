package com.foober.foober.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class RideBriefDisplay {
    private long id;
    private UserBriefDisplay driver;
    private Set<UserBriefDisplay> clients;
    private double price;
    private double distance;
    private String startLocation;
    private String endLocation;
    private String startTime;
    private String endTime;
    private boolean favorite;
    private double rating;
}
