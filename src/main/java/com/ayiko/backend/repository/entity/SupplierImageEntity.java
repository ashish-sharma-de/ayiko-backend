package com.ayiko.backend.repository.entity;

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
public class SupplierImageEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String imageUrl;
    private String imageType;
    private String imageTitle;
    private String imageDescription;
    private boolean isProfilePicture;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;
}
