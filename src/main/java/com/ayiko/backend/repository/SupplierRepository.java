package com.ayiko.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ayiko.backend.repository.entity.ProductEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepository extends JpaRepository<SupplierEntity, UUID> {
    Optional<SupplierEntity> findByEmailAddress(String emailAddress);
    List<SupplierEntity> findAllByEmailAddress(String emailAddress);


}

