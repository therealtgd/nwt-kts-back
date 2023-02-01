package com.foober.foober.repos;

import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.Ride;
import com.foober.foober.model.enumeration.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("SELECT r from Ride r WHERE r.status = 'IN_PROGRESS'")
    Optional<List<Ride>> getAllInProgress();
    List<Ride> getRideByStatusAndClientsContaining(RideStatus status, Client client);
    List<Ride> getRideByStatusAndStartTimeBetweenAndDriverOrderByStartTimeAsc(RideStatus status, Long start, Long end, Driver driver);
    List<Ride> getRideByStatusAndStartTimeBetweenAndClientsContainingOrderByStartTimeAsc(RideStatus status, Long start, Long end, Client client);
    List<Ride> getRideByStatusAndStartTimeBetweenOrderByStartTimeAsc(RideStatus status, Long start, Long end);
}
