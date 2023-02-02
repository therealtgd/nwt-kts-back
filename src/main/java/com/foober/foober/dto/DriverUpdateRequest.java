package com.foober.foober.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverUpdateRequest {
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
    @NotEmpty
    @Size(min = 6, max = 15)
    @Pattern(regexp = "[a-zA-Za-šA-Š-0-9\s]*")
    private String licencePlate;
    @NotEmpty
    @Pattern(regexp = "SEDAN|WAGON")
    private String vehicleType;
    @NotNull
    @Min(0)
    private int capacity;
    @NotNull
    private boolean petsAllowed;
    @NotNull
    private boolean babiesAllowed;
    @NotNull
    private boolean imageUploaded;

}
