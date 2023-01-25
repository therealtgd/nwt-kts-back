package com.foober.foober.repos;

import com.foober.foober.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUsername(String subject);
}
