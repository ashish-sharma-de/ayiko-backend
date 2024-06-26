package com.ayiko.backend.controller.core;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.*;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartPaymentConfirmationStatus;
import com.ayiko.backend.dto.cart.CartPaymentReceiptStatus;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.repository.core.entity.CartEntity;
import com.ayiko.backend.service.core.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderOTPService orderOTPService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private JWTTokenProvider tokenProvider;
    private final String ERROR_INVALID_TOKEN = "Token invalid, valid token required to access this API";
    private final String ERROR_INVALID_CART_ID = "Token invalid, valid token required to access this API";

    private final String ERROR_INVALID_SUPPLIER_ID = "Customer id doesn't match the customer in token";

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
            validateToken(authorizationHeader);
            CartDTO cartDTO = cartService.getCartById(id);
            if (cartDTO == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_CART_ID)).build();
            }
            return ResponseEntity.ok(cartDTO);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/sendForApproval")
    public ResponseEntity sendForApproval( @RequestHeader("Authorization") String authorizationHeader, @RequestBody CartDTO cartDTO) {
        try {

            UUID customerId = getCustomerIdFromToken(authorizationHeader);
            cartDTO.setCustomerId(customerId);
            cartDTO.setStatus(CartStatus.PENDING);
            if(cartDTO.getItems() == null || cartDTO.getItems().isEmpty()){
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Cart must have at least one item")).build();
            }
            return ResponseEntity.ok(cartService.saveCart(cartDTO));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{id}/acceptCart")
    public ResponseEntity acceptCart(@PathVariable UUID id,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            UUID tokenSupplierId = getSupplierIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (!cart.getSupplierId().equals(tokenSupplierId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.acceptCart(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{id}/rejectCart")
    public ResponseEntity rejectCart(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            UUID tokenSupplierId = getSupplierIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (cart.getSupplierId().equals(tokenSupplierId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.rejectCart(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{id}/addPaymentConfirmationStatus")
    public ResponseEntity addPaymentConfirmationStatus(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            UUID tokenCustomerId = getCustomerIdFromToken(authorizationHeader);
            CartDTO cart = cartService.getCartById(id);
            if (!cart.getCustomerId().equals(tokenCustomerId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to accept this cart")).build();
            }
            cartService.updateCartPaymentConfirmationStatus(id, CartPaymentConfirmationStatus.CONFIRMED);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
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
            CartEntity cartEntity = cartService.updateCartPaymentReceiptStatus(id, status);
            CartDTO cartDTO = orderService.createOrderForCart(cartEntity);
            orderOTPService.generateOTPForOrder(cartDTO.getOrderId());
            return ResponseEntity.ok(cartDTO);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
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
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            getCustomerIdFromToken(authorizationHeader);
            if(cartService.canDelete(id)){
                boolean deleted = cartService.deleteCart(id);
                if (deleted) {
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            }else{
                return ResponseEntity.unprocessableEntity().body("Cannot delete cart as order is already present for cart.");
            }
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }
}
