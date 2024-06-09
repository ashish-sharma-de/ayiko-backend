package com.ayiko.backend.service.core;

import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.dto.SupplierRatingDTO;

import java.util.List;
import java.util.UUID;

public interface SupplierRatingService {
    SupplierRatingDTO addSupplierRating(SupplierRatingDTO supplierRatingDTO);

    Double getAverageRatingForSupplier(UUID supplierId);
}
