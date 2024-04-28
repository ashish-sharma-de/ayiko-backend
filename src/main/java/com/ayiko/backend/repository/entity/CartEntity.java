package com.ayiko.backend.repository.entity;

import com.ayiko.backend.dto.cart.CartStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CartPaymentEntity paymentDetails;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CartItemEntity> items = new HashSet<>();

    @OneToOne
    private OrderEntity order;

    private UUID deliveryAddressId;

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
            this.items = new HashSet<>();
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
