package com.ayiko.backend.dto.order;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.dto.cart.AddressDTO;
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

    private SupplierDTO supplier;
    private CustomerDTO customer;

    private OrderStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private OrderDriverStatus driverStatus;

    private DriverDTO driver;
    private UUID driverId;

    private OrderPaymentDTO paymentDetails;

    private Set<OrderItemDTO> items;

    private boolean isAssignedToSelf;

    private AddressDTO deliveryAddress;
}
