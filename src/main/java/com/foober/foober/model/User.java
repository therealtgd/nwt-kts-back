package com.foober.foober.model;

import com.foober.foober.model.enumeration.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    private UUID Id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Role authority;
    private String image;
}