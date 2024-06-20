package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.OrderOTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface OrderOTPRepository extends JpaRepository<OrderOTPEntity, UUID> {

    OrderOTPEntity findByOrderId(UUID orderId);
}

