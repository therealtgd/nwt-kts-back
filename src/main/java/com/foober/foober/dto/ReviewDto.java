package com.foober.foober.dto;

import com.foober.foober.model.Review;

import static com.foober.foober.util.DtoConverter.longToTime;

public class ReviewDto {
    private UserBriefDisplay reviewer;
    private String timestamp;
    private int driverRating;
    private int vehicleRating;
    private String comment;

    public ReviewDto (Review review) {
        this.reviewer = new UserBriefDisplay(review.getClient());
        this.timestamp = longToTime(review.getTimeStamp());
        this.rating = review.getRating();
    }
}
