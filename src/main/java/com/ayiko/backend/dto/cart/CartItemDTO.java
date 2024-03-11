package com.ayiko.backend.dto.cart;

import com.ayiko.backend.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private UUID id;
    private UUID productId;
    private int quantity;
    private ProductDTO product;
}
