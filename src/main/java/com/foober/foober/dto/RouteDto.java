package com.foober.foober.dto;

import com.foober.foober.dto.ride.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteDto {
    long id;
    List<AddressDto> stops;
}
