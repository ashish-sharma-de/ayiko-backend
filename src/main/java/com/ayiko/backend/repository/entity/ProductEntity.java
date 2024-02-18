package com.ayiko.backend.repository.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
public class ProductEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private String category;
    private UUID supplierId;
    private String unitPrice;
    private String quantity;
    private String imageUrl;
    private boolean isAvailable;
    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDate updatedAt;
}
