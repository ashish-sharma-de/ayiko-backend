package com.ayiko.backend.service;

import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderDriverStatus;
import com.ayiko.backend.repository.entity.CartEntity;

import java.util.List;
import java.util.UUID;

public interface OrderOTPService {

    void generateOTPForOrder(UUID orderId);

    String getOTPForOrder(UUID orderId);

    boolean validateOTPForOrder(UUID orderId, String otp);
}
