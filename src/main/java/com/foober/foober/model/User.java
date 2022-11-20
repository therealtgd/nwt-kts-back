package com.foober.foober.model;

import com.foober.foober.model.enumeration.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int ID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Role authority;
    private String image;
}