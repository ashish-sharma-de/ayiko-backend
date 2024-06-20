package com.ayiko.backend.service.payment.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentUrlResponse {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("status")
    private int status;

    @JsonProperty("payment_token")
    private String paymentToken;

    @JsonProperty("payment_url")
    private String paymentUrl;

    @JsonProperty("state")
    private String state;

    @JsonProperty("user_msisdn")
    private String userMsisdn;

    // Getters and Setters

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserMsisdn() {
        return userMsisdn;
    }

    public void setUserMsisdn(String userMsisdn) {
        this.userMsisdn = userMsisdn;
    }
}
