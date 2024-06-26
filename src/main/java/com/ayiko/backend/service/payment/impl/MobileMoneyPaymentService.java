package com.ayiko.backend.service.payment.impl;

import com.ayiko.backend.repository.payment.PaymentRequestRepository;
import com.ayiko.backend.repository.payment.entity.PaymentRequestEntity;
import com.ayiko.backend.service.payment.PaymentService;
import com.ayiko.backend.service.payment.dto.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MobileMoneyPaymentService implements PaymentService {

    @Value("${bizao.CLIENT_ID}")
    private String bizaoClientId;
    @Value("${bizao.CLIENT_SECRET}")
    private String bizaoClientSecret;

    @Value("${bizao.baseurl}")
    private String bizaoBaseUrl;

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @Autowired
    private TokenService tokenService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MobileMoneyPaymentService.class);

    private String getAccessToken() {
        return tokenService.getAccessToken();
    }

    public PaymentUrlResponse generatePaymentUrl(PaymentRequest paymentRequest) throws UnsupportedEncodingException {

        logger.info("Processing create Payment Request - {}", paymentRequest);

        String orderIdStr = (paymentRequest.getUserType() + paymentRequest.getUserId().toString()).replace("-", "");
        String state = URLEncoder.encode(paymentRequest.toString(), "UTF-8");
        MobileMoneyPaymentRequest request = MobileMoneyPaymentRequest.builder()
                .currency("XOF")//Togo- XOF, CFA Ghana- GHS
                .order_id(orderIdStr)
                .amount("10")
                .return_url("https://ashish.eu.ngrok.io/api/v1/payment/success")
                .cancel_url("https://ashish.eu.ngrok.io/api/v1/payment/cancel")
                .reference("AYIKO_Sarl")
                .state(state)
                .build();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.getAccessToken());
        headers.set("mno-name", "orange");
        headers.set("country-code", "ci");
        headers.set("lang", "en");
        headers.set("channel", "web");
        headers.set("Content-Type", "application/json");

        HttpEntity<MobileMoneyPaymentRequest> entity = new HttpEntity<>(request, headers);

        try {
            PaymentUrlResponse paymentUrlResponse = restTemplate.postForObject(bizaoBaseUrl + "/mobilemoney/v2", entity, PaymentUrlResponse.class);
            PaymentRequestEntity paymentRequestEntity = getPaymentRequestEntity(paymentUrlResponse, paymentRequest);
            paymentRequestRepository.save(paymentRequestEntity);

            return paymentUrlResponse;
        } catch (Exception e) {
            logger.error("Error while processing create Payment Request - {}, exception- {}", paymentRequest, e);
        }
        return null;
    }

    private PaymentRequestEntity getPaymentRequestEntity(PaymentUrlResponse paymentUrlResponse, PaymentRequest paymentRequest) {
        return PaymentRequestEntity.builder()
                .paymentOrderId(paymentUrlResponse.getOrderId())
                .createdAt(LocalDateTime.now())
                .paymentUrl(paymentUrlResponse.getPaymentUrl())
                .payToken(paymentUrlResponse.getPaymentToken())
                .userId(paymentRequest.getUserId())
                .userType(paymentRequest.getUserType())
                .state(paymentUrlResponse.getState())
                .status(PaymentConstants.PAYMENT_STATUS_IN_PROGRESS)
                .amount(paymentRequest.getAmount())
                .build();
    }

    @Override
    public boolean isPaymentInProcessForCustomer(PaymentRequest paymentRequest) {
        Optional<PaymentRequestEntity> byCustomerId = paymentRequestRepository.findByUserIdAndUserType(paymentRequest.getUserId(), paymentRequest.getUserType());
        if (byCustomerId.isPresent()) {
            PaymentRequestEntity paymentRequestEntity = byCustomerId.get();
            if (paymentRequestEntity.getStatus().equals(PaymentConstants.PAYMENT_STATUS_IN_PROGRESS)) {
                if (paymentRequestEntity.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
                    //paymentRequestRepository.delete(paymentRequestEntity);
                    //TODO: Check with AYIKO about the time kitna lagega for status update.
                    //What happens when we have a payment link and tries to regenerate again and how long will it be valid?
                    return false;
                } else return true;
            }
        }
        return false;
    }

    public static String insertHyphensToUUID(String strippedUuid) {
        if (strippedUuid == null || strippedUuid.length() != 32) {
            throw new IllegalArgumentException("Input string must be exactly 32 characters long.");
        }

        // Reinsert hyphens at the appropriate positions: 8-4-4-4-12
        return new StringBuilder(strippedUuid)
                .insert(8, "-")
                .insert(13, "-")
                .insert(18, "-")
                .insert(23, "-")
                .toString();
    }

    private String getUUIDFromPaymentOrderId(String paymentOrderId) {
        int startIndex = 0;
        if (paymentOrderId.startsWith(PaymentConstants.USER_TYPE_CUSTOMER)) {
            startIndex = PaymentConstants.USER_TYPE_CUSTOMER.length();
        } else {
            startIndex = PaymentConstants.USER_TYPE_SUPPLIER.length();
        }
        String orderIdWithoutHyphen = paymentOrderId.substring(startIndex);
        return insertHyphensToUUID(orderIdWithoutHyphen);
    }

    @Override
    public void updatePaymentRequest(PaymentRequestResponse paymentResponse) {
        String orderId = getUUIDFromPaymentOrderId(paymentResponse.getOrderId());

        Optional<PaymentRequestEntity> paymentRequestEntityOptional = paymentRequestRepository.findByPaymentOrderId(paymentResponse.getOrderId());
        if(paymentRequestEntityOptional.isPresent()){
            PaymentRequestEntity entity = paymentRequestEntityOptional.get();
            entity.setStatus(paymentResponse.getStatus());

            paymentRequestRepository.save(entity);
        }
    }
}
