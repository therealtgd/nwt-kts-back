package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CancellationReason {
    private UUID Id;
    private String text;
    private Ride ride;
}