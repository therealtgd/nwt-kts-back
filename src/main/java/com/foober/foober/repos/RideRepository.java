package com.foober.foober.repos;

import com.foober.foober.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("SELECT r from Ride r WHERE r.status = 'IN_PROGRESS'")
    Optional<List<Ride>> getAllInProgress();

}
