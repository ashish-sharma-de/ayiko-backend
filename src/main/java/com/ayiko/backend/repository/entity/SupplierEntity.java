package com.ayiko.backend.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String companyName;
    private String ownerName;
    private String phoneNumber;
    private String mobileMoneyNumber;
    private String bankAccountNumber;
    private String city;
    @Column(unique = true)
    private String emailAddress;
    private String password; // Store hashed passwords only
    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDate updatedAt;
    private LocalDate lastLoginAt;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductEntity> products;

    private String profileImageUrl;
    private String businessImages;
    private String businessName;
    private String businessDescription;
    private Double rating;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

}
