package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByEmailAddress(String emailAddress);

}

