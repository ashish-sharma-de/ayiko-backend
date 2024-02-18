package com.ayiko.backend.repository;

import java.util.UUID;

import com.ayiko.backend.repository.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepository extends JpaRepository<SupplierEntity, UUID> {
}

