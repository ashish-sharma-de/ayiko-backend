package com.ayiko.backend.repository.entity;

import com.ayiko.backend.dto.cart.CartStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private CartPaymentEntity paymentDetails;

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

    public void addItem(CartItemEntity item) {
        if(this.items == null){
            this.items = new ArrayList<>();
        }
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItemEntity item) {
        if(this.items == null){
            return;
        }
        items.remove(item);
        item.setCart(null);
    }

}
