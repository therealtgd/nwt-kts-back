package com.foober.foober.repos;

import com.foober.foober.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPasswordAndEnabled(String username, String password, boolean enabled);

    boolean existsByUsername(String username);
}
