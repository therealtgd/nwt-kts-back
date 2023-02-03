package com.foober.foober.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private int driverRating;
    private int vehicleRating;
    private String comment;
}
