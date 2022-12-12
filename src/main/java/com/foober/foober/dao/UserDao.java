package com.foober.foober.dao;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {

    private final static List<UserDetails> APP_USERS = Arrays.asList(
        new User(
            "admin",
            "$2a$12$HMNmCbprltHU1daz2mzQp.gMklDPXU4FDOQTlMcn0.GdOiqIY4.6q", // "admin"
            Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ),
        new User(
            "user",
            "$2a$12$m9g0d/z3FpLJ9/EKK4TMieCtXk0cEPLaVzw8xXegyXRpqh392GwLi", // "user"
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        )
    );

    public UserDetails findUserByUsername(String username) throws UsernameNotFoundException {
        return APP_USERS
            .stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElseThrow(() -> new UsernameNotFoundException("User was not found."));
    }
}
