package com.ayiko.backend.repository.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String paymentOrderId;
    private UUID customerId;
    private String payToken;
    private String paymentUrl;
    private String notificationToken;
    private String state;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
