package com.ayiko.backend.dto.order;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private int quantity;
}
