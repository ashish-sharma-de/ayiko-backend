package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SupplierRatingDTO {

    public UUID id;
    public String message;
    public int rating;
    public UUID supplierId;
    public UUID customerId;
}
