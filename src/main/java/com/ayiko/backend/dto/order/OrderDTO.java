package com.ayiko.backend.dto.order;

import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.repository.entity.DriverStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class OrderDTO {

    private UUID id;
    private UUID supplierId;
    private UUID customerId;

    private OrderStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private OrderDriverStatus driverStatus;

    private DriverDTO driver;
    private UUID driverId;

    private OrderPaymentDTO paymentDetails;

    private Set<OrderItemDTO> items;

    private CartDTO cart;

    private boolean isAssignedToSelf;
}
