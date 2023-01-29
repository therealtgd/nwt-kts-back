package com.foober.foober.dto;

import com.foober.foober.validation.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class PasswordUpdateRequest {
    @NotEmpty
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String password;
    @NotEmpty
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String confirmPassword;

}
