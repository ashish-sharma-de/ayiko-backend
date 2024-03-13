package com.ayiko.backend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private CartPaymentConfirmationStatus customerStatus;
    private CartPaymentReceiptStatus supplierStatus;
}
