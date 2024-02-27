package com.ayiko.backend.repository.entity;

import com.ayiko.backend.dto.order.OrderPaymentMethod;
import com.ayiko.backend.dto.order.OrderPaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPaymentEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private OrderEntity order;
    private OrderPaymentMethod paymentMethod;
    private OrderPaymentStatus orderPaymentStatus;
    private LocalDate paymentDate;
}
