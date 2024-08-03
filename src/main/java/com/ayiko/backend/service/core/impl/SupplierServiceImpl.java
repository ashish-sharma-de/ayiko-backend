package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.repository.core.entity.SupplierEntity;
import com.ayiko.backend.repository.core.SupplierRepository;
import com.ayiko.backend.repository.core.entity.SupplierImageEntity;
import com.ayiko.backend.service.core.SupplierRatingService;
import com.ayiko.backend.service.core.SupplierService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SupplierRatingService supplierRatingService;

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
        Optional<SupplierEntity> byId = repository.findById(id);
        if (byId.isPresent()) {
            SupplierEntity supplierEntity = byId.get();
            supplierEntity.setBankAccountNumber(supplierDTO.getBankAccountNumber() != null ? supplierDTO.getBankAccountNumber() : supplierEntity.getBankAccountNumber());
            supplierEntity.setCity(supplierDTO.getCity() != null ? supplierDTO.getCity() : supplierEntity.getCity());
            supplierEntity.setCompanyName(supplierDTO.getCompanyName() != null ? supplierDTO.getCompanyName() : supplierEntity.getCompanyName());
            supplierEntity.setEmailAddress(supplierDTO.getEmailAddress() != null ? supplierDTO.getEmailAddress() : supplierEntity.getEmailAddress());
            supplierEntity.setMobileMoneyNumber(supplierDTO.getMobileMoneyNumber() != null ? supplierDTO.getMobileMoneyNumber() : supplierEntity.getMobileMoneyNumber());
            supplierEntity.setOwnerName(supplierDTO.getOwnerName() != null ? supplierDTO.getOwnerName() : supplierEntity.getOwnerName());
            supplierEntity.setPhoneNumber(supplierDTO.getPhoneNumber() != null ? supplierDTO.getPhoneNumber() : supplierEntity.getPhoneNumber());
            supplierEntity.setBusinessImageUrls(EntityDTOConverter.imageUrlsToString(supplierDTO.getBusinessImages()) != null ? EntityDTOConverter.imageUrlsToString(supplierDTO.getBusinessImages()) : supplierEntity.getBusinessImageUrls());
            supplierEntity.setBusinessName(supplierDTO.getBusinessName() != null ? supplierDTO.getBusinessName() : supplierEntity.getBusinessName());
            supplierEntity.setBusinessDescription(supplierDTO.getBusinessDescription() != null ? supplierDTO.getBusinessDescription() : supplierEntity.getBusinessDescription());
            supplierEntity.setProfileImageUrl(supplierDTO.getProfileImageUrl() != null ? supplierDTO.getProfileImageUrl() : supplierEntity.getProfileImageUrl());

            if (supplierDTO.getImages() != null) {
                supplierDTO.getImages().forEach(imageDTO -> {
                    SupplierImageEntity businessImage = SupplierImageEntity.builder()
                            .isProfilePicture(imageDTO.isProfilePicture())
                            .imageUrl(imageDTO.getImageUrl())
                            .imageDescription(imageDTO.getImageDescription())
                            .imageTitle(imageDTO.getImageTitle())
                            .supplier(supplierEntity).build();
                    supplierEntity.getBusinessImages().add(businessImage);
                });
            }
            SupplierEntity save = repository.save(supplierEntity);
            return EntityDTOConverter.convertSupplierEntityToSupplierDTO(save);
        }
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
        return repository.findAll().stream().map(this::getSupplier).toList();
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

    @Override
    public void addBusinessImage(UUID supplierId, ImageDTO imageDTO) {
        repository.findById(supplierId).ifPresent(supplierEntity -> {
            SupplierImageEntity businessImage = SupplierImageEntity.builder()
                    .isProfilePicture(imageDTO.isProfilePicture())
                    .imageUrl(imageDTO.getImageUrl())
                    .imageDescription(imageDTO.getImageDescription())
                    .imageTitle(imageDTO.getImageTitle())
                    .supplier(supplierEntity).build();
            supplierEntity.getBusinessImages().add(businessImage);
            repository.save(supplierEntity);
        });
    }

    @Override
    public void deleteBusinessImage(UUID id, ImageDTO imageDTO) {
        Optional<SupplierEntity> byId = repository.findById(id);
        if (byId.isPresent()) {
            SupplierEntity supplierEntity = byId.get();
            Set<SupplierImageEntity> images = supplierEntity.getBusinessImages();
            Optional<SupplierImageEntity> imageEntity = images.stream().filter(supplierImageEntity -> supplierImageEntity.getId().equals(imageDTO.getId())).findFirst();
            if(imageEntity.isPresent()){
                images.remove(imageEntity.get());
                supplierEntity.setBusinessImages(images);
                repository.save(supplierEntity);
            }
        }
    }

    @Override
    public List<SupplierDTO> searchSupplier(String searchQuery) {
        List<SupplierEntity> supplierEntityList = repository.findByCompanyNameContainingIgnoreCase(searchQuery);
        List<SupplierDTO> collect = supplierEntityList.stream().map(EntityDTOConverter::convertSupplierEntityToSupplierDTO).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SupplierDTO> findNearbySuppliers(double lat, double lon, double distance) {
        List<SupplierEntity> nearbySuppliers = repository.findNearbySuppliers(lat, lon, distance);
        return nearbySuppliers.stream().map(EntityDTOConverter::convertSupplierEntityToSupplierDTO).collect(Collectors.toList());
    }

    private SupplierDTO getSupplier(SupplierEntity entity){
        SupplierDTO supplierDTO = EntityDTOConverter.convertSupplierEntityToSupplierDTO(entity);
        supplierDTO.setRating(supplierRatingService.getAverageRatingForSupplier(entity.getId()));
        return supplierDTO;
    }
}
