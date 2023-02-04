package com.foober.foober.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CancellationReason")
public class CancellationReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    @OneToOne(fetch = FetchType.LAZY)
    private Ride ride;

    public CancellationReason(String text, Ride ride) {
        this.text = text;
        this.ride = ride;
    }
}