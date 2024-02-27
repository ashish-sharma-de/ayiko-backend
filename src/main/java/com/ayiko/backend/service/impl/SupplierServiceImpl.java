package com.ayiko.backend.service.impl;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.repository.entity.SupplierEntity;
import com.ayiko.backend.repository.SupplierRepository;
import com.ayiko.backend.service.SupplierService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        supplierDTO.setPassword(passwordEncoder.encode(supplierDTO.getPassword()));
        SupplierEntity save = repository.save(EntityDTOConverter.convertSupplierDTOToSupplierEntity(supplierDTO));
        return EntityDTOConverter.convertSupplierEntityToSupplierDTO(save);
    }

    @Override
    public SupplierDTO updateSupplier(UUID id, SupplierDTO supplierDTO) {
        repository.findById(id).ifPresent(supplierEntity -> {
            supplierEntity.setBankAccountNumber(supplierDTO.getBankAccountNumber());
            supplierEntity.setCity(supplierDTO.getCity());
            supplierEntity.setCompanyName(supplierDTO.getCompanyName());
            supplierEntity.setEmailAddress(supplierDTO.getEmailAddress());
            supplierEntity.setMobileMoneyNumber(supplierDTO.getMobileMoneyNumber());
            supplierEntity.setOwnerName(supplierDTO.getOwnerName());
            supplierEntity.setPhoneNumber(supplierDTO.getPhoneNumber());
            repository.save(supplierEntity);
        });
        return supplierDTO;
    }

    @Override
    public boolean deleteSupplier(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public SupplierDTO getSupplierById(UUID id) {
        return repository.findById(id).map(EntityDTOConverter::convertSupplierEntityToSupplierDTO).orElse(null);
    }

    @Override
    public SupplierDTO getSupplierByEmail(String email) {
        return repository.findByEmailAddress(email).map(EntityDTOConverter::convertSupplierEntityToSupplierDTO).orElse(null);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        return repository.findAll().stream().map(EntityDTOConverter::convertSupplierEntityToSupplierDTO).toList();
    }

    @Override
    public boolean resetPassword(UUID supplierId, String currentPassword, String newPassword) {
        SupplierEntity supplier = repository.findById(supplierId).orElse(null);
        if (supplier != null && passwordEncoder.matches(currentPassword, supplier.getPassword())) {
            supplier.setPassword(passwordEncoder.encode(newPassword));
            repository.save(supplier);
            return true;
        }
        return false;
    }

    @Override
    public String authenticateSupplier(LoginDTO loginDTO) {
        SupplierEntity supplier = repository.findByEmailAddress(loginDTO.getUsername()).orElse(null);
        if (supplier != null && passwordEncoder.matches(loginDTO.getPassword(), supplier.getPassword())) {
            return tokenProvider.generateToken(supplier.getEmailAddress());
        }
        return null;
    }

}
