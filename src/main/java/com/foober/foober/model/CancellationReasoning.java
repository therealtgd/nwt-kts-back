package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CancellationReasoning {
    private UUID ID;
    private String text;
    private Drive drive;
}