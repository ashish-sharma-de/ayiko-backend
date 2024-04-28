package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findAllBySupplierIdOrderByCreatedAtDesc(UUID supplierId);
    List<OrderEntity> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<OrderEntity> findAllByDriverIdOrderByCreatedAtDesc(UUID driverId);

}
