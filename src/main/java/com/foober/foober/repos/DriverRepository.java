package com.foober.foober.repos;

import com.foober.foober.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d from Driver d WHERE d.status = 'AVAILABLE' or d.status = 'BUSY'")
    List<Driver> findAllActive();
}
