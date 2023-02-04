package com.foober.foober.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DriverUnavailable extends RuntimeException {
    public DriverUnavailable(String driver) {
        super("Driver unavailable: " + driver);
    }
}
