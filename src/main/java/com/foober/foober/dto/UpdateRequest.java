package com.foober.foober.dto;

import com.foober.foober.validation.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {
    @NotEmpty
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š\s]*")
    private String displayName;
    @NotEmpty
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String username;
    @NotEmpty
    @Size(min = 9, max = 10)
    @Pattern(regexp = "[0-9]*")
    private String phoneNumber;
    @NotEmpty
    @Size(min = 3, max = 25)
    @Pattern(regexp = "[a-zA-Za-šA-Š\s]*")
    private String city;

}
