package com.foober.foober.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ClientRegistrationConfirmationDTO {

    @NotBlank
    private String token;

}
