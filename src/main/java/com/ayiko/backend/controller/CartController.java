package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.*;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartPaymentConfirmationStatus;
import com.ayiko.backend.dto.cart.CartPaymentReceiptStatus;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.service.CartService;
import com.ayiko.backend.service.CustomerService;
import com.ayiko.backend.service.SupplierService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private JWTTokenProvider tokenProvider;
    private final String ERROR_INVALID_TOKEN = "Token invalid, valid token required to access this API";
    private final String ERROR_INVALID_CART_ID = "Token invalid, valid token required to access this API";

    private final String ERROR_INVALID_SUPPLIER_ID = "Customer id doesn't match the customer in token";

//    @PostMapping
//    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO, @RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            UUID customerId = getCustomerIdFromToken(authorizationHeader);
//            cartDTO.setCustomerId(customerId);
//            cartDTO.setStatus(CartStatus.PENDING);
//            return ResponseEntity.ok(cartService.saveCart(cartDTO));
//        } catch (Exception e) {
//            return handleException(e);
//        }
//    }

    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return token;
    }

    private UUID getCustomerIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        CustomerDTO customerDTO = customerService.getCustomerByEmail(username);
        if (customerDTO == null || !customerDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_SUPPLIER_ID);
        }
        return customerDTO.getId();
    }

    private UUID getSupplierIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        SupplierDTO supplierDTO = supplierService.getSupplierByEmail(username);
        if (supplierDTO == null || !supplierDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_SUPPLIER_ID);
        }
        return supplierDTO.getId();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCart(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            getCustomerIdFromToken(authorizationHeader);
            CartDTO cartDTO = cartService.getCartById(id);
            if (cartDTO == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_CART_ID)).build();
            }
            return ResponseEntity.ok(cartDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/sendForApproval")
    public ResponseEntity sendForApproval( @RequestHeader("Authorization") String authorizationHeader, @RequestBody CartDTO cartDTO) {
        try {

            UUID customerId = getCustomerIdFromToken(authorizationHeader);
            cartDTO.setCustomerId(customerId);
            cartDTO.setStatus(CartStatus.PENDING);
            return ResponseEntity.ok(cartService.saveCart(cartDTO));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/{id}/acceptCart")
    public ResponseEntity acceptCart(@PathVariable UUID id,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            UUID tokenSupplierId = getSupplierIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (cart.getSupplierId() != tokenSupplierId) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.acceptCart(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/{id}/rejectCart")
    public ResponseEntity rejectCart(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            UUID tokenSupplierId = getSupplierIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (cart.getSupplierId() != tokenSupplierId) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.rejectCart(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/{id}/addPaymentConfirmationStatus")
    public ResponseEntity addPaymentConfirmationStatus(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") CartPaymentConfirmationStatus status) {
        try {
            UUID tokenCustomerId = getCustomerIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (!cart.getCustomerId().equals(tokenCustomerId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.updateCartPaymentConfirmationStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/{id}/addPaymentReceiptStatus")
    public ResponseEntity addPaymentReceiptStatus(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") CartPaymentReceiptStatus status) {
        try {
            UUID tokenSupplier = getSupplierIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (!cart.getSupplierId().equals(tokenSupplier)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.updateCartPaymentReceiptStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    //TODO: Implement the following methods
    // @GetMapping("/{customerId}/addItem")

    @PutMapping("/{id}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @RequestBody CartDTO cartDTO) {
        try {
            getCustomerIdFromToken(authorizationHeader);
            CartDTO updatedCart = cartService.updateCart(cartDTO);
            if (updatedCart != null) {
                return ResponseEntity.ok(updatedCart);
            } else {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_CART_ID)).build();
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            getCustomerIdFromToken(authorizationHeader);
            boolean deleted = cartService.deleteCart(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception e) {
        if (e instanceof RuntimeException && (e.getMessage().equals(ERROR_INVALID_TOKEN) || e.getMessage().equals(ERROR_INVALID_SUPPLIER_ID))) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage())).build();
        }
        logger.error("Error occurred: {}", e.getMessage(), e);
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())).build();
    }
}
