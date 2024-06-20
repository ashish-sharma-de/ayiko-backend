package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findAllBySupplierIdOrderByCreatedAtDesc(UUID supplierId);
    List<OrderEntity> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<OrderEntity> findAllByDriverIdOrderByCreatedAtDesc(UUID driverId);

}
