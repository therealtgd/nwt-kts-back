package com.foober.foober.model;

import com.foober.foober.model.enumeration.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingPayment {
    private int ID;
    private Client client;
    private Drive drive;
    private PaymentStatus status;
    private double amount;
}