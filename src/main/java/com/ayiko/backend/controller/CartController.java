package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.CartDTO;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.service.CartService;
import com.ayiko.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private JWTTokenProvider tokenProvider;
    private final String ERROR_INVALID_TOKEN = "Token invalid, valid token required to access this API";
    private final String ERROR_INVALID_CART_ID = "Token invalid, valid token required to access this API";

    private final String ERROR_INVALID_SUPPLIER_ID = "Customer id doesn't match the customer in token";

    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            UUID customerId = getCustomerIdFromToken(authorizationHeader);
            cartDTO.setCustomerId(customerId);
            return ResponseEntity.ok(cartService.saveCart(cartDTO));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private UUID getCustomerIdFromToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        String username = tokenProvider.getUsernameFromJWT(token);
        CustomerDTO customerDTO = customerService.getCustomerByEmail(username);
        if (customerDTO == null || !customerDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_SUPPLIER_ID);
        }
        return customerDTO.getId();
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

    @GetMapping("/{id}/sendForApproval")
    public ResponseEntity sendForApproval(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        //TODO: Implement this
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{supplierId}/supplier")
    public ResponseEntity<List<CartDTO>> getCartsForSupplier(@PathVariable UUID supplierId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            return ResponseEntity.ok(cartService.getCartsBySupplierId(supplierId));
        }catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{customerId}/customer")
    public ResponseEntity<List<CartDTO>> getCartsForCustomer(@PathVariable UUID customerId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            return ResponseEntity.ok(cartService.getCartsByCustomerId(customerId));
        }catch (Exception e) {
            return handleException(e);
        }
    }

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
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())).build();
    }
}
