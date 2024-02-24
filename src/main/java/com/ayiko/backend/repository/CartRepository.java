package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartRepository  extends JpaRepository<CartEntity, UUID> {

    List<CartEntity> findAllByCustomerId(UUID customerId);
    List<CartEntity> findAllBySupplierId(UUID supplierId);
}
