package com.foober.foober.repos;

import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.Ride;
import com.foober.foober.model.enumeration.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByStatus(RideStatus status);

    @Query("SELECT r from Ride r WHERE r.status = 'IN_PROGRESS' and r.driver.isReserved = false")
    Optional<List<Ride>> getAllInProgressNotReserved();

    List<Ride> getRideByStatusAndStartTimeBetweenAndDriverOrderByStartTimeAsc(RideStatus status, Long start, Long end, Driver driver);
    List<Ride> getRideByStatusAndStartTimeBetweenAndClientsContainingOrderByStartTimeAsc(RideStatus status, Long start, Long end, Client client);
    List<Ride> getRideByStatusAndStartTimeBetweenOrderByStartTimeAsc(RideStatus status, Long start, Long end);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ride r WHERE r.status = 'ON_ROUTE' AND r.status = 'IN_PROGRESS' AND :client MEMBER OF r.clients")
    boolean activeRideByUserIsPresent(@Param("client") Client client);

    @Query("SELECT r FROM Ride r WHERE r.status = 'ON_ROUTE' OR r.status = 'IN_PROGRESS' AND :client MEMBER OF r.clients")
    Optional<Ride> getActiveRideByClient(Client client);

    @Query("SELECT DISTINCT r FROM Ride r WHERE r.status = 'ON_ROUTE' OR r.status = 'IN_PROGRESS' and r.driver.id = ?1")
    Optional<Ride> getInProgressRideByClientId(Long id);

    Optional<Ride> getRideByStatusAndDriverId(RideStatus rideStatus, Long id);
}
