package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderDriverStatus;
import com.ayiko.backend.dto.order.OrderPaymentDTO;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private DriverService driverService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderOTPService orderOTPService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            if (order == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}/otp")
    public ResponseEntity<String> getOrderOTP(@PathVariable UUID id) {
        try {
            String otp = orderOTPService.getOTPForOrder(id);
            if (otp == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            return ResponseEntity.ok(otp);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/supplier")
    public ResponseEntity<List<OrderDTO>> getOrdersForSupplier(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            SupplierDTO supplierDTO = getSupplierIdFromToken(authorizationHeader);
            return ResponseEntity.ok(orderService.getOrdersForSupplier(supplierDTO.getId()));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/customer")
    public ResponseEntity<List<OrderDTO>> getOrdersForCustomer(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            CustomerDTO customerDTO = getCustomerIdFromToken(authorizationHeader);
            return ResponseEntity.ok(orderService.getOrdersForCustomer(customerDTO.getId()));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/driver")
    public ResponseEntity<List<OrderDTO>> getOrdersForDriver(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            DriverDTO driverDTO = getDriverIdFromToken(authorizationHeader);
            return ResponseEntity.ok(orderService.getOrdersForDriver(driverDTO.getId()));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{orderId}/assignToSelf")
    public ResponseEntity assignToSelf(@RequestHeader("Authorization") String authorizationHeader,
                                       @PathVariable UUID orderId) {
        try {
            SupplierDTO supplierDTO = getSupplierIdFromToken(authorizationHeader);
            orderService.assignDriverToOrder(supplierDTO.getId(), orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{orderId}/assignDriver/{driverId}")
    public ResponseEntity assignToDriver(@RequestHeader("Authorization") String authorizationHeader,
                                         @PathVariable UUID orderId, @PathVariable UUID driverId) {
        try {
            validateToken(authorizationHeader);
            if (driverService.getDriverById(driverId) == null)
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            if (orderService.getOrderById(orderId) == null)
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();

            orderService.assignDriverToOrder(driverId, orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{orderId}/driverAccepted")
    public ResponseEntity driverAccepted(@RequestHeader("Authorization") String authorizationHeader,
                                         @PathVariable UUID orderId) {
        return updateDriverStatusForOrder(authorizationHeader, orderId, OrderDriverStatus.DRIVER_ACCEPTED);
    }

    @PostMapping("/{orderId}/driverRejected")
    public ResponseEntity driverRejected(@RequestHeader("Authorization") String authorizationHeader,
                                         @PathVariable UUID orderId) {
        return updateDriverStatusForOrder(authorizationHeader, orderId, OrderDriverStatus.DRIVER_REJECTED);
    }

    @PostMapping("/{orderId}/startDelivery")
    public ResponseEntity startDelivery(@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable UUID orderId) {
        return updateDriverStatusForOrder(authorizationHeader, orderId, OrderDriverStatus.DRIVER_DISPATCHED);
    }

    @PostMapping("/{orderId}/validateOrderOTP/{otp}")
    public ResponseEntity validateDeliveryOTP(@RequestHeader("Authorization") String authorizationHeader,
                                           @PathVariable UUID orderId, @PathVariable String otp) {
        boolean isValidOTP = orderOTPService.validateOTPForOrder(orderId, otp);
        if (isValidOTP) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid OTP")).build();
        }
    }

    @PostMapping("/{orderId}/completeDelivery")
    public ResponseEntity completeDelivery(@RequestHeader("Authorization") String authorizationHeader,
                                           @PathVariable UUID orderId) {
        return updateDriverStatusForOrder(authorizationHeader, orderId, OrderDriverStatus.DELIVERY_COMPLETED);
    }

    private ResponseEntity<OrderDTO> updateDriverStatusForOrder(String authorizationHeader, UUID orderId, OrderDriverStatus driverStatus) {
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            if (order.isAssignedToSelf()) {
                orderService.updateDriverStatus(driverStatus, orderId);
                return ResponseEntity.ok(orderService.getOrderById(orderId));
            }
            DriverDTO driverDTO = getDriverIdFromToken(authorizationHeader);
            if (driverDTO.getId().equals(order.getDriverId())) {
                orderService.updateDriverStatus(driverStatus, orderId);
                return ResponseEntity.ok(orderService.getOrderById(orderId));
            } else {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "DriverId doesn't match assigned Driver")).build();
            }
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    private SupplierDTO getSupplierIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        SupplierDTO supplierDTO = supplierService.getSupplierByEmail(username);
        if (supplierDTO == null || !supplierDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ExceptionHandler.ERROR_INVALID_TOKEN);
        }
        return supplierDTO;
    }

    private DriverDTO getDriverIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        DriverDTO driverDTO = driverService.getDriverByEmail(username);
        if (driverDTO == null || !driverDTO.getEmail().equals(username)) {
            throw new RuntimeException(ExceptionHandler.ERROR_INVALID_TOKEN);
        }
        return driverDTO;
    }

    private CustomerDTO getCustomerIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        CustomerDTO customerDTO = customerService.getCustomerByEmail(username);
        if (customerDTO == null || !customerDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ExceptionHandler.ERROR_INVALID_TOKEN);
        }
        return customerDTO;
    }

    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ExceptionHandler.ERROR_INVALID_TOKEN);
        }
        return token;
    }

}
