package com.foober.foober.model;

import com.foober.foober.model.enumeration.RideStatus;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ride")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Set<Address> route;
    @ManyToMany(mappedBy = "rides", fetch = FetchType.LAZY)
    private Set<Client> clients;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private double distance;
    @Enumerated(EnumType.STRING)
    private RideStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
    @Column(nullable = false)
    private Long startTime;
    @Column(nullable = false)
    private Long endTime;

    public Ride(Set<Address> route,
                double price,
                double distance,
                RideStatus status,
                Driver driver,
                Long startTime,
                Long endTime) {
        this.route = route;
        this.clients = new HashSet<>();
        this.price = price;
        this.distance = distance;
        this.status = status;
        this.driver = driver;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public void addClient(Client client) {
        clients.add(client);
        client.getRides().add(this);
    }
}