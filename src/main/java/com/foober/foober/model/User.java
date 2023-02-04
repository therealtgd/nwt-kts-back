package com.foober.foober.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "mySeqGenV1", sequenceName = "mySeqV1", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGenV1")
    protected Long id;
    @Column(name = "username", nullable = false, columnDefinition = "TEXT", unique = true)
    protected String username;
    @Column(name = "email", nullable = false, columnDefinition = "TEXT", unique = true)
    protected String email;
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    protected String password;
    @Column(name = "display_name", nullable = false, columnDefinition = "TEXT")
    protected String displayName;
    @Column(name = "phone_number", columnDefinition = "TEXT")
    protected String phoneNumber;
    @Column(name = "city", columnDefinition = "TEXT")
    protected String city;
    @ManyToMany(fetch = FetchType.EAGER)
    protected Set<Role> authorities = new HashSet<>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    protected Image image;
    @Column(name="enabled", nullable = false)
    protected boolean enabled;
    @Column(name="provider")
    protected String provider;
    @Column(name = "provider_user_id")
    protected String providerUserId;
    @Column(name = "credits")
    protected int credits;

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void addCredits(int amount) {
        this.setCredits(this.credits + amount);
    }
}
