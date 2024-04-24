package com.ayiko.backend.service.impl;

import com.ayiko.backend.dto.LocationDTO;
import com.ayiko.backend.dto.order.OrderTrackingDTO;
import com.ayiko.backend.repository.OrderTrackingRepository;
import com.ayiko.backend.repository.entity.OrderTrackingEntity;
import com.ayiko.backend.service.OrderTrackingService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderTrackingServiceImpl implements OrderTrackingService {
    @Autowired
    OrderTrackingRepository orderTrackingRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public void trackOrder(OrderTrackingDTO orderTrackingDTO) {
        OrderTrackingEntity orderTrackingEntity = orderTrackingRepository.getOrderTrackingEntityByOrderId(orderTrackingDTO.getOrderId());
        if (orderTrackingEntity == null) {
            orderTrackingEntity = OrderTrackingEntity.builder()
                    .orderId(orderTrackingDTO.getOrderId())
                    .location(geometryFactory.createPoint(
                            new Coordinate(orderTrackingDTO.getDriverLocation().getLongitude(),
                                    orderTrackingDTO.getDriverLocation().getLatitude())))
                    .build();
        } else {
            orderTrackingEntity.setLocation(
                    geometryFactory.createPoint(
                            new Coordinate(orderTrackingDTO.getDriverLocation().getLongitude(),
                                    orderTrackingDTO.getDriverLocation().getLatitude())));
        }
        orderTrackingRepository.save(orderTrackingEntity);

    }

    @Override
    public OrderTrackingDTO getOrderTracking(UUID orderId) {
        OrderTrackingEntity orderTrackingEntity = orderTrackingRepository.getOrderTrackingEntityByOrderId(orderId);
        if (orderTrackingEntity == null) {
            return null;
        }
        return OrderTrackingDTO.builder()
                .orderId(orderTrackingEntity.getOrderId())
                .driverLocation(LocationDTO.builder()
                                .latitude(orderTrackingEntity.getLocation().getY())
                                .longitude(orderTrackingEntity.getLocation().getX()).build())
                .build();
    }
}
