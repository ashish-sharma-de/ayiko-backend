package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.dto.SupplierRatingDTO;
import com.ayiko.backend.repository.SupplierRatingRepository;
import com.ayiko.backend.repository.entity.SupplierRatingEntity;
import com.ayiko.backend.service.core.SupplierRatingService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SupplierRatingServiceImpl implements SupplierRatingService {

    @Autowired
    private SupplierRatingRepository repository;

    @Override
    public SupplierRatingDTO addSupplierRating(SupplierRatingDTO supplierRatingDTO) {
        SupplierRatingEntity ratingEntity = EntityDTOConverter.convertSupplierRatingDTOtoEntity(supplierRatingDTO);
        return EntityDTOConverter.convertSupplierRatingEntityToDTO(repository.save(ratingEntity));
    }

    @Override
    public Double getAverageRatingForSupplier(UUID supplierId){
        return repository.findAverageRatingBySupplierId(supplierId);
    }

}
