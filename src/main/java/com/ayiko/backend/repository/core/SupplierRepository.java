package com.ayiko.backend.repository.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ayiko.backend.repository.core.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepository extends JpaRepository<SupplierEntity, UUID> {
    Optional<SupplierEntity> findByEmailAddress(String emailAddress);
    List<SupplierEntity> findAllByEmailAddress(String emailAddress);


}

