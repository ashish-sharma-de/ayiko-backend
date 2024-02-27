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
public class OrderItemEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID productId;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
