package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findBySupplierId(UUID supplierId);
    List<ProductEntity> findByCategory(String category);
    boolean existsByCategory(String category);
}

