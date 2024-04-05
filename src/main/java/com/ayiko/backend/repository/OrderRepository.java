package com.ayiko.backend.repository;

import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.repository.entity.CartEntity;
import com.ayiko.backend.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findAllBySupplierId(UUID supplierId);
    List<OrderEntity> findAllByCustomerId(UUID customerId);
    List<OrderEntity> findAllByDriverId(UUID driverId);

}
