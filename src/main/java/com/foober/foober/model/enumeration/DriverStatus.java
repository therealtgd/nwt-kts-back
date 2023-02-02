package com.foober.foober.model.enumeration;

public enum DriverStatus {
    AVAILABLE,
    PENDING, // Driver has a ride assigned but the client didn't order yet
    BUSY,
    AWAY,
    OFFLINE
}