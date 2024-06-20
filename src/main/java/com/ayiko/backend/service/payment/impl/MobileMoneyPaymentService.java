package com.ayiko.backend.service.payment.impl;

import com.ayiko.backend.repository.payment.PaymentRequestRepository;
import com.ayiko.backend.repository.payment.entity.PaymentRequestEntity;
import com.ayiko.backend.service.payment.PaymentService;
import com.ayiko.backend.service.payment.dto.BizaoTokenResponse;
import com.ayiko.backend.service.payment.dto.MobileMoneyPaymentRequest;
import com.ayiko.backend.service.payment.dto.PaymentRequestResponse;
import com.ayiko.backend.service.payment.dto.PaymentUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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


    private String generateBase64Token() {
        String credentials = bizaoClientId + ":" + bizaoClientSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + generateBase64Token());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<BizaoTokenResponse> response = restTemplate.exchange(bizaoBaseUrl + "/token", HttpMethod.POST, request, BizaoTokenResponse.class);

        BizaoTokenResponse tokenResponse = response.getBody();
        String token = tokenResponse.getAccess_token();
        return token;
    }

    public PaymentUrlResponse createPaymentUrl(UUID customerId) {

        customerId = UUID.randomUUID();
        String orderIdStr = customerId.toString().replace("-","");
        MobileMoneyPaymentRequest request = MobileMoneyPaymentRequest.builder()
                .currency("XOF")//Togo- XOF, CFA Ghana- GHS
                .order_id(orderIdStr)
                .amount("10")
                .return_url("https://ashish.eu.ngrok.io/api/v1/payment/success")
                .cancel_url("https://ashish.eu.ngrok.io/api/v1/payment/cancel")
                .reference("AYIKO_Sarl")
                .state("Test State")
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
            return restTemplate.postForObject(bizaoBaseUrl + "/mobilemoney/v1", entity, PaymentUrlResponse.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isPaymentInProcessForCustomer(UUID customerId) {
        Optional<PaymentRequestEntity> byCustomerId = paymentRequestRepository.findByCustomerId(customerId);
        if(byCustomerId.isPresent()){
            PaymentRequestEntity paymentRequestEntity = byCustomerId.get();
            if(paymentRequestEntity.getStatus().equals("IN_PROGRESS")){
                if(paymentRequestEntity.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(15))){
                    paymentRequestRepository.delete(paymentRequestEntity);
                    return false;
                }else return true;
            }
        }
        return false;
    }

    @Override
    public void addPaymentRequest(PaymentUrlResponse paymentUrlResponse) {
        //TODO add paymend Request in database.
    }


}
