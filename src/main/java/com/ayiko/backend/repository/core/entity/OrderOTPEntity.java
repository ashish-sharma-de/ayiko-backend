package com.ayiko.backend.repository.core.entity;

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
public class OrderOTPEntity {
    @Id
    @GeneratedValue
    UUID id;
    UUID orderId;
    String otp;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    int attempts;
}
