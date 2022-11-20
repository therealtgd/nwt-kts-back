package com.foober.foober.model;

import com.foober.foober.model.enumeration.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PendingPayment {
    private UUID ID;
    private Client client;
    private Drive drive;
    private PaymentStatus status;
    private double amount;
}