package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private String category;
    private UUID supplierId;
    private String unitPrice;
    private String quantity;
    private List<String> imageUrl;
    private boolean isAvailable;
    private String createdAt;
    private String updatedAt;

}
