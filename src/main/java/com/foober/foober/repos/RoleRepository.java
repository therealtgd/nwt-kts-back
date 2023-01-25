package com.foober.foober.repos;

import com.foober.foober.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleAdmin);
}
