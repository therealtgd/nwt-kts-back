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

    @Query("SELECT c from Client c JOIN c.rides r where c.id = ?1 AND (r.status = 'ON_ROUTE' OR r.status = 'IN_PROGRESS') ")
    Optional<Client> getClientInActiveRideById(long id);

    @Query("SELECT c.username from Client c where c.username LIKE :query%")
    Optional<List<String>> getClientByUsernameStartsWith(@Param("query") String query);
}
