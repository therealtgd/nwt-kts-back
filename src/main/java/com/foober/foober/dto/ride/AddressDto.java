package com.foober.foober.dto.ride;

import com.foober.foober.dto.LatLng;
import lombok.Data;

@Data
public class AddressDto {
    private String address;
    private LatLng coordinates;
}
