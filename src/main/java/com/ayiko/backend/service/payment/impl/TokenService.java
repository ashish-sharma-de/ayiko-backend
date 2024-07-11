package com.ayiko.backend.service.payment.impl;

import com.ayiko.backend.controller.payment.PaymentController;
import com.ayiko.backend.repository.payment.BizaoAccessTokenRepository;
import com.ayiko.backend.repository.payment.entity.BizaoAccessTokenEntity;
import com.ayiko.backend.service.payment.dto.BizaoTokenResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    @Autowired
    private BizaoAccessTokenRepository tokenRepository;
//      Bizao Prod
//    private static final String CLIENT_ID = "o5UNyFyq0fiOy8XnXcfFpEQ7UeAa";
//    private static final String CLIENT_SECRET = "JMlV41JvfDCrxywEBjbbn6pon20a";
//    private static final String TOKEN_URL = "https://api.bizao.com/token";

    //      Bizao PreProd
    private static final String CLIENT_ID = "o5UNyFyq0fiOy8XnXcfFpEQ7UeAa";
    private static final String CLIENT_SECRET = "JMlV41JvfDCrxywEBjbbn6pon20a";
    private static final String TOKEN_URL = "https://api.bizao.com/token";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TokenService.class);


    public String getAccessToken() {
        BizaoAccessTokenEntity token = tokenRepository.findById("Bearer").orElse(null);
        if (token == null || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            token = generateAndSaveToken();
        }
        return token.getToken();
    }

    /*
    *    RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + generateBase64Token());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<BizaoTokenResponse> response = restTemplate.exchange(bizaoBaseUrl + "/token", HttpMethod.POST, request, BizaoTokenResponse.class);

        BizaoTokenResponse tokenResponse = response.getBody();
        String token = tokenResponse.getAccess_token();
    *
    * */

    private BizaoAccessTokenEntity generateAndSaveToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        logger.info("Content-Type = " + headers.get("Content-Type"));
        logger.info("Headers = " + headers.get("Authorization"));

        System.out.println("Headers = " + headers.get("Authorization"));
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TOKEN_URL);

        ResponseEntity<BizaoTokenResponse> response = restTemplate.exchange(builder.toUriString() , HttpMethod.POST, request, BizaoTokenResponse.class);
        BizaoTokenResponse tokenResponse = response.getBody();

        BizaoAccessTokenEntity tokenEntity = BizaoAccessTokenEntity.builder().token(tokenResponse.getAccess_token())
                        .id("Bearer").expiresAt(LocalDateTime.now().plusMinutes(10)).build();

        tokenRepository.save(tokenEntity);
        logger.info("Token generated...");
        return tokenEntity;
    }
}
