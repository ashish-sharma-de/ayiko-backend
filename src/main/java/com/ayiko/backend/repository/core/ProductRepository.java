package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findBySupplierId(UUID supplierId);
    List<ProductEntity> findByCategory(String category);
    boolean existsByCategory(String category);
}

