package com.foober.foober.repos;

import com.foober.foober.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
