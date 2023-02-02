package com.foober.foober.dto;

import lombok.Value;

@Value
public class UserInfo {
    private String image;
    private String displayName;
    private String username;
    private String email;
    private String role;
    private String phoneNumber;
    private String City;
}
