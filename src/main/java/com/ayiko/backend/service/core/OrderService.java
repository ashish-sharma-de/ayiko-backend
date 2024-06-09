package com.ayiko.backend.service.core;

import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderDriverStatus;
import com.ayiko.backend.repository.entity.CartEntity;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    CartDTO createOrderForCart(CartEntity cartEntity);

    OrderDTO getOrderById(UUID orderId);

    List<OrderDTO> getOrdersForSupplier(UUID id);

    List<OrderDTO> getOrdersForCustomer(UUID id);

    void assignDriverToOrder(UUID driverId, UUID orderId);

    List<OrderDTO>  getOrdersForDriver(UUID driverId);

    void updateDriverStatus(OrderDriverStatus driverStatus, UUID orderId);
}
