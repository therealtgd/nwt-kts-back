package com.foober.foober.dto.ride;

import com.foober.foober.dto.LatLng;
import com.foober.foober.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressDto {
    private String address;
    private LatLng coordinates;

    public AddressDto(Address address) {
        this.address = address.getStreetAddress();
        this.coordinates = new LatLng(address.getLatitude(), address.getLongitude());
    }
}
