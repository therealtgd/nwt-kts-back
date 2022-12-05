package com.foober.foober.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cancellation_reasons")
public class CancellationReason {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    @OneToOne(fetch = FetchType.LAZY)
    private Ride ride;

    public CancellationReason(String text, Ride ride) {
        this.text = text;
        this.ride = ride;
    }
}