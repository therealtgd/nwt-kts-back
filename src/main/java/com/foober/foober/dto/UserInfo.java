package com.foober.foober.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserInfo {
    private String id;
    private String image;
    private String displayName;
    private String email;
    private List<String> roles;
}
