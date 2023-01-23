package com.foober.foober.dto;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ClientRegistration {
    @NotEmpty
    @Pattern(regexp = "^[a-zA-z]{2,20}$")
    private String firstName;
    @NotEmpty
    @Pattern(regexp = "^[a-zA-z]{2,20}$")
    private String lastName;
    @NotEmpty
    @Pattern(regexp = "^[a-zA-z0-9]{2,20}$")
    private String username;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "^[0-9]{6,10}$")
    private String phoneNumber;
    @NotEmpty
    @Pattern(regexp = "^[a-zA-z0-9]{6,20}$")
    private String password;
    @NotEmpty
    @Pattern(regexp = "^[a-zA-z0-9]{6,20}$")
    private String confirmPassword;
    @NotEmpty
    private String image;
}
