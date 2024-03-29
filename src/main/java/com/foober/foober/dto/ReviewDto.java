package com.foober.foober.dto;

import com.foober.foober.model.Review;
import lombok.Getter;

import static com.foober.foober.util.DtoConverter.longToTime;

@Getter
public class ReviewDto {
    private UserBriefDisplay reviewer;
    private String timestamp;
    private int driverRating;
    private int vehicleRating;
    private String comment;

    public ReviewDto (Review review) {
        this.reviewer = new UserBriefDisplay(review.getClient());
        this.timestamp = longToTime(review.getTimeStamp());
        this.driverRating = review.getDriverRating();
        this.vehicleRating = review.getVehicleRating();
        this.comment = review.getComment();
    }
}
