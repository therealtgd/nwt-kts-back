package com.foober.foober.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Value
public class ApiResponse {
    private boolean success;
    private String message;

}