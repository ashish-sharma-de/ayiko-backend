package com.ayiko.backend.service.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MobileMoneyPaymentRequest {

    private String currency;
    private String order_id;
    private String amount;
    private String return_url;
    private String cancel_url;
    private String reference;
    private String state;
}
