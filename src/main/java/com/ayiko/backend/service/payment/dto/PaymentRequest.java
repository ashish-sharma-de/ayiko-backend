package com.ayiko.backend.service.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentRequest {

    String userType;
    UUID userId;
    Double amount;
}
