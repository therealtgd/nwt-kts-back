package com.foober.foober.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity(name = "PendingDriverChanges")
@Table(name = "pending_driver_changes")
public class PendingDriverChanges {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    private UUID id;
    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    private String firstName;
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    private String lastName;
    @Column(name = "profile_picture", nullable = false, columnDefinition = "TEXT")
    private String profilePicture;
    @OneToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
    @Column(nullable = false)
    private Long timeStamp;

    public PendingDriverChanges(String firstName,
                                String lastName,
                                String profilePicture,
                                Vehicle vehicle,
                                Long timeStamp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.vehicle = vehicle;
        this.timeStamp = timeStamp;
    }
}