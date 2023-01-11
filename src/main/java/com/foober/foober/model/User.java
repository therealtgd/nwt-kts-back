package com.foober.foober.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name = "Users")
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;
    @Column(name = "username", nullable = false, columnDefinition = "TEXT", unique = true)
    protected String username;
    @Column(name = "email", nullable = false, columnDefinition = "TEXT", unique = true)
    protected String email;
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    protected String password;
    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    protected String firstName;
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    protected String lastName;
    @ManyToOne
    protected Role authority;
    @Column(name = "image", columnDefinition = "TEXT")
    protected String image;
    @Column(name="enabled", nullable = false)
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}