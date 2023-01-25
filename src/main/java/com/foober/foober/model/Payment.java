package com.foober.foober.model;

import com.foober.foober.model.enumeration.PaymentStatus;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Column(nullable = false)
    private double amount;

    public Payment(Client client,
                   Ride ride,
                   PaymentStatus status,
                   double amount) {
        this.client = client;
        this.ride = ride;
        this.status = status;
        this.amount = amount;
    }
}