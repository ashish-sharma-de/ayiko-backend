package com.ayiko.backend.repository.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<ProductEntity> products = new HashSet<>();

    private String profileImageUrl;
    private String businessImageUrls;
    private String businessName;
    private String businessDescription;
    private Double rating;
    private Long totalRating;

    private Point location;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SupplierImageEntity> businessImages = new HashSet<>();


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
