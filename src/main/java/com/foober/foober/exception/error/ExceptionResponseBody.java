package com.foober.foober.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponseBody {
    private final Integer status;
    private final String message;
}