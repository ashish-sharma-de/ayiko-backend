package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.SupplierRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


public interface SupplierRatingRepository extends JpaRepository<SupplierRatingEntity, UUID> {

    @Query("SELECT AVG(e.rating) FROM SupplierRatingEntity e WHERE e.supplierId = :supplierId")
    Double findAverageRatingBySupplierId(UUID supplierId);
}

