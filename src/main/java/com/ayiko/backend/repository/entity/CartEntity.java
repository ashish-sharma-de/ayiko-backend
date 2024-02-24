package com.ayiko.backend.repository.entity;

import com.ayiko.backend.dto.CartItemDTO;
import com.ayiko.backend.dto.CartStatus;
import com.ayiko.backend.service.CartService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID supplierId;
    private UUID customerId;

    private CartStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItemEntity> items;

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
