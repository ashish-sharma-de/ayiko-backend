package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.CartItemEntity;
import com.ayiko.backend.repository.entity.DriverEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Driver;
import java.util.Optional;
import java.util.UUID;

public interface DriverEntityRepository extends JpaRepository<DriverEntity, UUID> {
    public Optional<DriverEntity> findByEmail(String email);
}
