package com.foober.foober.dto;

import lombok.Value;

@Value
public class AuthResponse {
    private String accessToken;
    private UserInfo user;

}
