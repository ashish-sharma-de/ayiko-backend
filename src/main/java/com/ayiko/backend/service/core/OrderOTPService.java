package com.ayiko.backend.service.core;

import java.util.UUID;

public interface OrderOTPService {

    void generateOTPForOrder(UUID orderId);

    String getOTPForOrder(UUID orderId);

    boolean validateOTPForOrder(UUID orderId, String otp);
}
