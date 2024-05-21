package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.OrderOTPEntity;
import com.ayiko.backend.repository.entity.SupplierRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


public interface OrderOTPRepository extends JpaRepository<OrderOTPEntity, UUID> {

    OrderOTPEntity findByOrderId(UUID orderId);
}

