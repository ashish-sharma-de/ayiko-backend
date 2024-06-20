package com.ayiko.backend.service.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestResponse {

    private Meta meta;
    private String status;
    private int amount;
    private String orderId;
    private String currency;
    private String reference;
    private String date;
    private String countryCode;
    private String state;
    private long userMsisdn;
    private String intTransactionId;
    private String extTransactionId;
    private String statusDescription;

    @Data
    @Builder
    public static class Meta {
        private String type;
        private String source;
        private String channel;
    }
}


