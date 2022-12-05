package com.foober.foober.repos;

import com.foober.foober.model.PendingDriverChanges;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PendingDriverChangesRepository extends JpaRepository<PendingDriverChanges, UUID> {
}
