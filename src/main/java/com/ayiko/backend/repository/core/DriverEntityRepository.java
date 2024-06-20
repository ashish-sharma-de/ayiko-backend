package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverEntityRepository extends JpaRepository<DriverEntity, UUID> {
    public Optional<DriverEntity> findByEmail(String email);
    public Optional<List<DriverEntity>> findAllBySupplierId(UUID supplierId);
}
