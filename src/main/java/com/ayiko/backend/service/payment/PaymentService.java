package com.ayiko.backend.service.payment;

import com.ayiko.backend.service.payment.dto.PaymentRequest;
import com.ayiko.backend.service.payment.dto.PaymentRequestResponse;
import com.ayiko.backend.service.payment.dto.PaymentUrlResponse;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public interface PaymentService {

    PaymentUrlResponse generatePaymentUrl(PaymentRequest paymentRequest) throws UnsupportedEncodingException;
    boolean isPaymentInProcessForCustomer(PaymentRequest paymentRequest);

    void updatePaymentRequest(PaymentRequestResponse paymentResponse);
}
