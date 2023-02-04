package com.foober.foober.repos;

import com.foober.foober.model.Review;
import com.foober.foober.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> getReviewsByRide(Ride ride);
}
