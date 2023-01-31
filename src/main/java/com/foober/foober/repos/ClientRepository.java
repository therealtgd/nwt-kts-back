package com.foober.foober.repos;

import com.foober.foober.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUsername(String subject);

    @Query("SELECT c FROM Client c WHERE c.username IN (:usernames)")
    Set<Client> findUsersByUsernames(@Param("usernames") List<String> usernames);
}
