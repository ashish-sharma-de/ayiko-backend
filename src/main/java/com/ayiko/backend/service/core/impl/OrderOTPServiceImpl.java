package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.repository.OrderOTPRepository;
import com.ayiko.backend.repository.entity.OrderOTPEntity;
import com.ayiko.backend.service.core.OrderOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderOTPServiceImpl implements OrderOTPService {

    @Autowired
    private OrderOTPRepository orderOTPRepository;

    @Override
    public void generateOTPForOrder(UUID orderId) {

        Random random = new Random();
        int otp = random.nextInt(9999);
        if (otp < 1000) {
            otp += 1000;
        }
        OrderOTPEntity orderOTPEntity = OrderOTPEntity.builder()
                .orderId(orderId)
                .otp(String.valueOf(otp))
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(5))
                .attempts(0)
                .build();
        orderOTPRepository.save(orderOTPEntity);
    }

    @Override
    public String getOTPForOrder(UUID orderId) {
        OrderOTPEntity orderOTPEntity = orderOTPRepository.findByOrderId(orderId);
        return orderOTPEntity.getOtp();
    }

    @Override
    public boolean validateOTPForOrder(UUID orderId, String otp) {
        OrderOTPEntity orderOTPEntity = orderOTPRepository.findByOrderId(orderId);
//        if(orderOTPEntity.getExpiresAt().isBefore(LocalDateTime.now())){
//            return false;
//        }
        if (orderOTPEntity.getOtp().equals(otp)) {
            orderOTPRepository.delete(orderOTPEntity);
            return true;
        }
        orderOTPEntity.setAttempts(orderOTPEntity.getAttempts() + 1);
        orderOTPRepository.save(orderOTPEntity);
        return false;
    }
}
