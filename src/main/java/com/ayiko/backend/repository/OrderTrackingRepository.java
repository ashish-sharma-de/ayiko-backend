package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.OrderTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTrackingRepository extends JpaRepository<OrderTrackingEntity, UUID> {
    public OrderTrackingEntity getOrderTrackingEntityByOrderId(UUID orderId);
}
