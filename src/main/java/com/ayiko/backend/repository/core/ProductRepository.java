package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;


public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
    List<ProductEntity> findBySupplierId(UUID supplierId);
    List<ProductEntity> findByCategory(String category);
    boolean existsByCategory(String category);
    List<ProductEntity> findBySupplierIdAndNameContainingIgnoreCase(UUID supplierId, String name);

}

