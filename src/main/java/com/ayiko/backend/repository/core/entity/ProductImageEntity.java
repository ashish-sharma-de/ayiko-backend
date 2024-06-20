package com.ayiko.backend.repository.core.entity;

import jakarta.persistence.*;
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
public class ProductImageEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String imageUrl;
    private String imageType;
    private String imageTitle;
    private String imageDescription;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
