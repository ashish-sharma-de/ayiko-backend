package com.ayiko.backend.repository;

import com.ayiko.backend.repository.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
}

