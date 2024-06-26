package com.ayiko.backend.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenRefreshService {

    @Autowired
    private TokenService tokenService;

    // Refresh every 24 hours
    @Scheduled(fixedRate = 86400000)
    public void refreshToken() {
        tokenService.getAccessToken();
    }
}

