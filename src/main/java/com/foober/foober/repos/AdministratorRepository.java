package com.foober.foober.repos;

import com.foober.foober.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdministratorRepository extends JpaRepository<Admin, UUID> {
}
