package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@ToString
public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private String category;
    private UUID supplierId;
    private SupplierDTO supplierDTO;
    private String unitPrice;
    private String quantity;
    private List<String> imageUrlList;
    private Set<ImageDTO> imageUrl;
    private boolean isAvailable;
    private String createdAt;
    private String updatedAt;
    private String productCategory;

}
