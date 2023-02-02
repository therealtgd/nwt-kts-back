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
import java.util.Set;

public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("SELECT r from Ride r WHERE r.status = 'IN_PROGRESS'")
    Optional<List<Ride>> getAllInProgress();
    List<Ride> getRideByStatusAndClientsContaining(RideStatus status, Client client);
    List<Ride> getRideByStatusAndStartTimeBetweenAndDriverOrderByStartTimeAsc(RideStatus status, Long start, Long end, Driver driver);
    List<Ride> getRideByStatusAndStartTimeBetweenAndClientsContainingOrderByStartTimeAsc(RideStatus status, Long start, Long end, Client client);
    List<Ride> getRideByStatusAndStartTimeBetweenOrderByStartTimeAsc(RideStatus status, Long start, Long end);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ride r WHERE r.status = 'ON_ROUTE' AND r.status = 'IN_PROGRESS' AND :client MEMBER OF r.clients")
    boolean activeRideByUserIsPresent(@Param("client") Client client);

    @Query("SELECT r FROM Ride r WHERE r.status = 'ON_ROUTE' OR r.status = 'IN_PROGRESS' AND :client MEMBER OF r.clients")
    Optional<Ride> getActiveRideByClient(Client client);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ride r WHERE r.status = 'ON_ROUTE' or r.status = 'IN_PROGRESS' AND :clients MEMBER OF r.clients")
    boolean clientsInActiveRide(Set<Client> clients);

}
