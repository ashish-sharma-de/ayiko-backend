package com.ayiko.backend.repository.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ayiko.backend.repository.core.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SupplierRepository extends JpaRepository<SupplierEntity, UUID> {
    Optional<SupplierEntity> findByEmailAddress(String emailAddress);
    List<SupplierEntity> findByCompanyNameContainingIgnoreCase(String companyName);
    @Query(value = "SELECT * FROM supplierentity WHERE ST_DWithin(location, ST_MakePoint(:lon, :lat)::geography, :distance)", nativeQuery = true)
    List<SupplierEntity> findNearbySuppliers(@Param("lat") double lat, @Param("lon") double lon, @Param("distance") double distance);

}

