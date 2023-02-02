package com.foober.foober.dto.ride;

import com.foober.foober.dto.LatLng;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressDto {
    private String address;
    private LatLng coordinates;
}
