package com.foober.foober.dto;

import com.foober.foober.validation.PasswordMatches;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@PasswordMatches
public class DriverSignUpRequest {
    @NotEmpty
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š\s]*")
    private String displayName;
    @NotEmpty
    @Size(min = 2, max = 64)
    @Email
    private String email;
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
    @NotEmpty
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String password;
    @NotEmpty
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String confirmPassword;
    @NotNull
    private boolean imageUploaded;
    @NotNull
    @Min(0)
    private int capacity;
    @NotEmpty
    @Size(min = 6, max = 15)
    @Pattern(regexp = "[a-zA-Za-šA-Š-0-9\s]*")
    private String licencePlate;
    @NotEmpty
    @Pattern(regexp = "SEDAN|WAGON")
    private String vehicleType;
    @NotNull
    private boolean petsAllowed;
    @NotNull
    private boolean babiesAllowed;

}
