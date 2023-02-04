package com.foober.foober.dto;

import com.foober.foober.model.Review;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDto {
    private int driverRating;
    private int vehicleRating;
    private String comment;

    public ReviewDto (Review review) {
        this.driverRating = review.getDriverRating();
        this.vehicleRating = review.getVehicleRating();
        this.comment = review.getComment();
    }
}
