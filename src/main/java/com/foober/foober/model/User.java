package com.foober.foober.model;

import com.foober.foober.model.enumeration.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class User {
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
    @Enumerated(EnumType.STRING)
    protected Role authority;
    @Column(name = "image", columnDefinition = "TEXT")
    protected String image;

}