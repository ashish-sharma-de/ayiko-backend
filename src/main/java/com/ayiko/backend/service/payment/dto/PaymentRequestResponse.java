package com.ayiko.backend.service.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestResponse {

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("status")
    private String status;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("order-id")
    private String orderId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("date")
    private String date;

    @JsonProperty("country-code")
    private String countryCode;

    @JsonProperty("state")
    private String state;

    @JsonProperty("user_msisdn")
    private long userMsisdn;

    @JsonProperty("intTransaction-id")
    private String intTransactionId;

    @JsonProperty("extTransaction-id")
    private String extTransactionId;

    @JsonProperty("statusDescription")
    private String statusDescription;

    @Data
    @Builder
    public static class Meta {
        @JsonProperty("type")
        private String type;

        @JsonProperty("source")
        private String source;

        @JsonProperty("channel")
        private String channel;
    }
}


