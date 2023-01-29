package com.foober.foober.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserInfo {
    private String image;
    private String displayName;
    private String username;
    private String email;
    private String role;
}
