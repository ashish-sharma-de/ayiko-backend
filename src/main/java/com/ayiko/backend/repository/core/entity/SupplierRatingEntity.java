package com.ayiko.backend.repository.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierRatingEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID supplierId;
    private UUID customerId;

    private String message;
    private int rating;
}
