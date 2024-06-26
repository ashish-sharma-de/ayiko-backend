package com.ayiko.backend.repository.payment.entity;

import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizaoAccessTokenEntity {
    @Id
    private String id;  // Use token type as the primary key
    private String token;
    private LocalDateTime expiresAt;
}

