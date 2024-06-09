package com.ayiko.backend.controller.core;

import com.ayiko.backend.dto.LocationDTO;
import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderTrackingDTO;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.service.core.OrderService;
import com.ayiko.backend.service.core.OrderTrackingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/orderTracking")
public class OrderTrackingController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OrderTrackingController.class);

    @Autowired
    private OrderTrackingService orderTrackingService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/trackOrder/{orderId}")
    public ResponseEntity trackOrder(@PathVariable UUID orderId, @RequestBody LocationDTO orderLocation) {
        try {
            OrderDTO orderById = orderService.getOrderById(orderId);
            if(orderById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            OrderTrackingDTO orderTracking = OrderTrackingDTO.builder().orderId(orderId).driverLocation(orderLocation).build();
            orderTrackingService.trackOrder(orderTracking);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/trackOrder/{orderId}")
    public ResponseEntity<OrderTrackingDTO> getOrderTracking(@PathVariable UUID orderId) {
        try {
            OrderDTO orderById = orderService.getOrderById(orderId);
            if(orderById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            OrderTrackingDTO orderTracking = orderTrackingService.getOrderTracking(orderId);
            return ResponseEntity.ok(orderTracking);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

}
