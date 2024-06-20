package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.OrderTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTrackingRepository extends JpaRepository<OrderTrackingEntity, UUID> {
    public OrderTrackingEntity getOrderTrackingEntityByOrderId(UUID orderId);
}
