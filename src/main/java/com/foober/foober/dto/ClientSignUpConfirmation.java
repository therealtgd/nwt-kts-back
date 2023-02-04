package com.foober.foober.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ClientSignUpConfirmation {

    @NotBlank
    private String token;

}
