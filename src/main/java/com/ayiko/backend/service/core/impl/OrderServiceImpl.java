package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderDriverStatus;
import com.ayiko.backend.dto.order.OrderStatus;
import com.ayiko.backend.repository.*;
import com.ayiko.backend.repository.entity.CartEntity;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.OrderEntity;
import com.ayiko.backend.service.core.OrderService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    DriverEntityRepository driverRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public CartDTO createOrderForCart(CartEntity cartEntity) {
        OrderEntity order = EntityDTOConverter.convertCartEntityToOrderEntity(cartEntity);
        CustomerEntity customer = customerRepository.findById(cartEntity.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        order.setDeliveryAddressId(cartEntity.getDeliveryAddressId());

        OrderEntity save = orderRepository.save(order);
        cartEntity.setOrder(save);

        CartEntity updatedCart = cartRepository.save(cartEntity);
        CartDTO cartDTO = EntityDTOConverter.convertCartEntityToCartDTO(updatedCart);
        if(updatedCart.getDeliveryAddressId() != null){
            addressRepository.findById(updatedCart.getDeliveryAddressId()).ifPresent(addressEntity -> cartDTO.setDeliveryAddress(EntityDTOConverter.convertAddressEntityToDTO(addressEntity)));
        }
        return cartDTO;
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        return getOrderDTO(order);
    }

    private OrderDTO getOrderDTO(OrderEntity orderEntity) {
        if(orderEntity == null){
            return null;
        }
        CartEntity cart = cartRepository.findByOrder(orderEntity);
        CartDTO cartDTO = EntityDTOConverter.convertCartEntityToCartDTO(cart);
        cartDTO.setSupplier(EntityDTOConverter.convertSupplierEntityToSupplierDTO(supplierRepository.findById(cartDTO.getSupplierId()).orElse(null)));
        cartDTO.setCustomer(EntityDTOConverter.convertCustomerEntityToCustomerDTO(customerRepository.findById(cartDTO.getCustomerId()).orElse(null)));
        if(cart.getDeliveryAddressId() != null){
            addressRepository.findById(cart.getDeliveryAddressId()).ifPresent(addressEntity -> cartDTO.setDeliveryAddress(EntityDTOConverter.convertAddressEntityToDTO(addressEntity)));
        }
        cartDTO.getItems().forEach(cartItemDTO -> {
            ProductDTO productDTO = EntityDTOConverter.convertProductEntityToDTO(productRepository.findById(cartItemDTO.getProductId()).orElse(null));
            cartItemDTO.setProduct(productDTO);
        });

        OrderDTO orderDTO = EntityDTOConverter.convertOrderEntityToDTO(orderEntity, cartDTO);
        if (orderEntity.getDriverId() != null && !orderEntity.getSupplierId().equals(orderEntity.getDriverId())) {
            orderDTO.setDriver(orderEntity.getDriverId() != null
                    ? EntityDTOConverter.convertDriverEntityToDTO(driverRepository.findById(orderDTO.getDriverId()).orElse(null))
                    : null);
        }
        return orderDTO;
    }

    @Override
    public List<OrderDTO> getOrdersForSupplier(UUID supplierId) {
        return orderRepository.findAllBySupplierIdOrderByCreatedAtDesc(supplierId).stream().map(this::getOrderDTO).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersForCustomer(UUID customerId) {
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(this::getOrderDTO).filter(Objects::nonNull).collect(Collectors.toList());
    }

    //TODO How to identify the driver if the driver is suppiler or driver
    @Override
    public void assignDriverToOrder(UUID driverId, UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        orderEntity.setDriverId(driverId);
        orderEntity.setDriverStatus(OrderDriverStatus.DRIVER_ASSIGNED);
        orderRepository.save(orderEntity);
    }

    @Override
    public List<OrderDTO> getOrdersForDriver(UUID driverId) {
        return orderRepository.findAllByDriverIdOrderByCreatedAtDesc(driverId).stream().map(this::getOrderDTO).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void updateDriverStatus(OrderDriverStatus driverStatus, UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        orderEntity.setDriverStatus(driverStatus);
        if (driverStatus == OrderDriverStatus.DELIVERY_COMPLETED) {
            orderEntity.setOrderStatus(OrderStatus.DELIVERED);
        }
        orderRepository.save(orderEntity);
    }
}