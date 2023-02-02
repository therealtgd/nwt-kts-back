package com.foober.foober.repos;

import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d from Driver d WHERE d.status = 'AVAILABLE' or d.status = 'BUSY' or d.status = 'PENDING'")
    List<Driver> findAllActive();

    @Query(
        "SELECT d FROM Driver d"
        + " JOIN d.vehicle v WHERE d.status = 'AVAILABLE' and d.isReserved = false AND v.type = ?1 AND (?2 = false OR v.petsAllowed = ?2) AND (?3 = false OR v.babiesAllowed = ?3)"
        + " ORDER BY ABS(v.latitude - ?4) + ABS(v.longitude - ?5) ASC"
    )
    Optional<List<Driver>> findNearestFreeDriver(
        VehicleType vehicleType,
        boolean petsAllowed,
        boolean babiesAllowed,
        double lat,
        double lng
    );

    @Query("SELECT d from Driver d WHERE d.status = ?1")
    List<Driver> findAllByStatus(DriverStatus status);
}
