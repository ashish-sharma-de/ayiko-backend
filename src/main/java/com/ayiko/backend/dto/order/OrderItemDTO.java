package com.ayiko.backend.dto.order;

import com.ayiko.backend.dto.ProductDTO;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private int quantity;
    private ProductDTO product;
}
