package com.foober.foober.repos;

import com.foober.foober.model.CancellationReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CancellationReasonRepository extends JpaRepository<CancellationReason, Long> {
}
