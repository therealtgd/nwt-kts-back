package com.foober.foober.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserBriefDisplay {
    private String displayName;
    private String username;
    private String image;
}
