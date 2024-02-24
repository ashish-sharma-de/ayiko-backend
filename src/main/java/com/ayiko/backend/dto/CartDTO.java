package com.ayiko.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private UUID id;
    private UUID supplierId;
    private UUID customerId;
    private List<CartItemDTO> items;
    private CartStatus status;
}
