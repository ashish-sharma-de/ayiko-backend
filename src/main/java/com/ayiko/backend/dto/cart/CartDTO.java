package com.ayiko.backend.dto.cart;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.SupplierDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private UUID id;
    private UUID supplierId;
    private UUID customerId;
    private Set<CartItemDTO> items;
    private CartStatus status;
    private SupplierDTO supplier;
    private CustomerDTO customer;
    private PaymentDTO paymentDetails;
    private UUID orderId;
    private AddressDTO deliveryAddress;
}
