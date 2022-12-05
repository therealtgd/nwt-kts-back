package com.foober.foober.model;

import com.foober.foober.model.enumeration.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "Payment")
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;
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