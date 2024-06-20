package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByEmailAddress(String emailAddress);

}

