package com.foober.foober.repos;

import com.foober.foober.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
