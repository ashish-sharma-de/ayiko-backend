package com.ayiko.backend.service.impl;

import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderDriverStatus;
import com.ayiko.backend.dto.order.OrderStatus;
import com.ayiko.backend.repository.CartRepository;
import com.ayiko.backend.repository.CustomerRepository;
import com.ayiko.backend.repository.OrderRepository;
import com.ayiko.backend.repository.entity.CartEntity;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.DriverStatus;
import com.ayiko.backend.repository.entity.OrderEntity;
import com.ayiko.backend.service.OrderService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public OrderDTO createOrderForCart(CartEntity cartEntity) {
        OrderEntity order = EntityDTOConverter.convertCartEntityToOrderEntity(cartEntity);
        CustomerEntity customer = customerRepository.findById(cartEntity.getCustomerId()).get();
        order.setDeliveryLocation(customer.getLocation());

        OrderEntity save = orderRepository.save(order);
        cartEntity.setOrder(save);

        cartRepository.save(cartEntity);

        return EntityDTOConverter.convertOrderEntityToDTO(save);
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        return EntityDTOConverter.convertOrderEntityToDTO(orderRepository.findById(orderId).orElse(null));
    }

    @Override
    public List<OrderDTO> getOrdersForSupplier(UUID supplierId) {
        return orderRepository.findAllBySupplierId(supplierId).stream().map(EntityDTOConverter::convertOrderEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersForCustomer(UUID customerId) {
        return orderRepository.findAllByCustomerId(customerId).stream().map(EntityDTOConverter::convertOrderEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public void assignDriverToOrder(UUID driverId, UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        orderEntity.setDriverId(driverId);
        orderEntity.setDriverStatus(OrderDriverStatus.DRIVER_ASSIGNED);
        orderRepository.save(orderEntity);
    }

    @Override
    public List<OrderDTO> getOrdersForDriver(UUID driverId) {
        return orderRepository.findAllByDriverId(driverId).stream().map(EntityDTOConverter::convertOrderEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public void updateDriverStatus(OrderDriverStatus driverStatus, UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        orderEntity.setDriverStatus(driverStatus);
        if(driverStatus == OrderDriverStatus.DELIVERY_COMPLETED) {
            orderEntity.setOrderStatus(OrderStatus.DELIVERED);
        }
        orderRepository.save(orderEntity);
    }
}
