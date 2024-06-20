package com.ayiko.backend.service.payment;

import com.ayiko.backend.service.payment.dto.PaymentUrlResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PaymentService {

    PaymentUrlResponse createPaymentUrl(UUID orderId);
    boolean isPaymentInProcessForCustomer(UUID customerId);
    void addPaymentRequest(PaymentUrlResponse paymentUrlResponse);
}
