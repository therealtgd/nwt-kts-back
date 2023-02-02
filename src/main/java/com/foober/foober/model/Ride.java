package com.foober.foober.model;

import com.foober.foober.model.enumeration.RideStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    @Column()
    private Long startTime;
    @Column()
    private Long endTime;
    @Column(nullable = false)
    private long eta;

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

    public Ride(Driver driver, Set<Address> route, double price, double distance) {
        this.driver = driver;
        this.route = route;
        this.price = price;
        this.distance = distance;
        this.clients = new HashSet<>();
        this.startTime = 0L;
        this.endTime = 0L;
        this.eta = 0;
        this.status = RideStatus.ON_ROUTE;
    }

    public void addClient(Client client) {
        clients.add(client);
        client.getRides().add(this);
    }
}