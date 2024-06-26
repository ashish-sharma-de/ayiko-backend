package com.ayiko.backend.repository.core.entity;

import com.ayiko.backend.dto.order.OrderDriverStatus;
import com.ayiko.backend.dto.order.OrderStatus;
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
public class OrderEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID supplierId;
    private UUID customerId;

    private OrderStatus orderStatus;

    private OrderDriverStatus driverStatus;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private UUID driverId;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderPaymentEntity paymentDetails;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<OrderItemEntity> items = new HashSet<>();

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

}
