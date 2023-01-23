package com.foober.foober.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDisplayDTO {
    private String image;
    private String firstName;
    private String lastName;
    private String accountType;
}
