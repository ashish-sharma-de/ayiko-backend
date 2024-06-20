package com.ayiko.backend.repository.payment;

import com.ayiko.backend.repository.payment.entity.PaymentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequestEntity, UUID> {
    Optional<PaymentRequestEntity> findByCustomerId(UUID customerId);
}