package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.SupplierEntity;
import com.ayiko.backend.repository.entity.SupplierRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SupplierRatingRepository extends JpaRepository<SupplierRatingEntity, UUID> {

    @Query("SELECT AVG(e.rating) FROM SupplierRatingEntity e WHERE e.supplierId = :supplierId")
    Double findAverageRatingBySupplierId(UUID supplierId);
}

