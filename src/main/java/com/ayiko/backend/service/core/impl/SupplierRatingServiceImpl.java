package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.dto.SupplierRatingDTO;
import com.ayiko.backend.repository.core.SupplierRatingRepository;
import com.ayiko.backend.repository.core.entity.SupplierRatingEntity;
import com.ayiko.backend.service.core.SupplierRatingService;
import com.ayiko.backend.service.core.SupplierService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SupplierRatingServiceImpl implements SupplierRatingService {

    @Autowired
    private SupplierRatingRepository repository;

    @Autowired
    private SupplierService supplierService;

    @Override
    public SupplierRatingDTO addSupplierRating(SupplierRatingDTO supplierRatingDTO) {
        SupplierRatingEntity ratingEntity = EntityDTOConverter.convertSupplierRatingDTOtoEntity(supplierRatingDTO);
        SupplierRatingEntity supplierRatingEntity = repository.save(ratingEntity);
        Double averageRating = this.getAverageRatingForSupplier(supplierRatingDTO.getSupplierId());
        Long totalRatingCount = this.getTotalRatingCountForSupplier(supplierRatingDTO.getSupplierId());
        supplierService.updateSupplierRating(supplierRatingDTO, averageRating, totalRatingCount);
        return EntityDTOConverter.convertSupplierRatingEntityToDTO(supplierRatingEntity);
    }

    @Override
    public Double getAverageRatingForSupplier(UUID supplierId){
        return repository.findAverageRatingBySupplierId(supplierId);
    }

    @Override
    public Long getTotalRatingCountForSupplier(UUID supplierId) {
        return repository.countAllBySupplierId(supplierId);
    }

}
