package com.ayiko.backend.service.payment.impl;

import com.ayiko.backend.service.payment.PaymentService;
import com.ayiko.backend.service.payment.dto.BizaoTokenResponse;
import com.ayiko.backend.service.payment.dto.MobileMoneyPaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.Random;

@Service
public class MobileMoneyPaymentService implements PaymentService {

    @Value("${bizao.CLIENT_ID}")
    private String bizaoClientId;

    @Value("${bizao.CLIENT_SECRET}")
    private String bizaoClientSecret;

    @Value("${bizao.baseurl}")
    private String bizaoBaseUrl;


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

    public String createPaymentUrl() {

        Random random = new Random();
        int id = random.nextInt(1000);
        MobileMoneyPaymentRequest request = MobileMoneyPaymentRequest.builder()
                .currency("XOF")//Togo- XOF, CFA Ghana- GHS
                .order_id("ORDER"+id)
                .amount("10")
                .return_url("https://ashish.eu.ngrok.io/api/v1/payment/success")
                .cancel_url("https://ashish.eu.ngrok.io/api/v1/payment/cancel")
                .reference("Test Payment Request")
                .state("Test State")
                .build();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.getAccessToken());
        headers.set("mno-name", "orange");
        headers.set("country-code", "sn");
        headers.set("lang", "fr");
        headers.set("channel", "web");
        headers.set("Content-Type", "application/json");

        HttpEntity<MobileMoneyPaymentRequest> entity = new HttpEntity<>(request, headers);

        try {

            return restTemplate.postForObject(bizaoBaseUrl + "/mobilemoney/v1", entity, String.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
