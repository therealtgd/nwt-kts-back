package com.foober.foober.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Complaint {
    private int ID;
    private String text;
    private Drive drive;
    private Client client;
}