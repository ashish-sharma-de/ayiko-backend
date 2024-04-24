package com.ayiko.backend.service;

import com.ayiko.backend.dto.order.OrderTrackingDTO;

import java.util.UUID;

public interface OrderTrackingService {
    void trackOrder(OrderTrackingDTO orderTrackingDTO);
    public OrderTrackingDTO getOrderTracking(UUID orderId);
}
