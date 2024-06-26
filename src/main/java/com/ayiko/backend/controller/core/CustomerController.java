package com.ayiko.backend.controller.core;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.cart.AddressDTO;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.service.core.CartService;
import com.ayiko.backend.service.core.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final String ERROR_INVALID_TOKEN = "Token is empty or invalid, valid token required to access this API";
    private final String ERROR_DUPLICATE_EMAIL = "Email already present.";

    private final String ERROR_INVALID_USERNAME = "Username invalid.";

    private final String ERROR_INVALID_ID = "Specified customer id is invalid";

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO customerByEmail = customerService.getCustomerByEmail(customerDTO.getEmailAddress());
        if (customerByEmail != null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ERROR_DUPLICATE_EMAIL)).build();
        }
        return ResponseEntity.ok(customerService.createCustomer(customerDTO));
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/getByToken")
    public CustomerDTO getCustomerByToken(@RequestHeader("Authorization") String authorizationHeader) {
        UUID customerIdFromToken = getCustomerIdFromToken(authorizationHeader);
        return customerService.getCustomerById(customerIdFromToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable UUID id) {
        CustomerDTO customerById = customerService.getCustomerById(id);
        if (customerById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        } else {
            return ResponseEntity.ok(customerById);
        }
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Boolean> resetPassword(@PathVariable UUID id, @RequestParam String currentPassword, @RequestParam String newPassword) {
        CustomerDTO customerById = customerService.getCustomerById(id);
        if (customerById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        return ResponseEntity.ok(customerService.resetPassword(id, currentPassword, newPassword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable UUID id, @RequestBody CustomerDTO customerDTO) {
        CustomerDTO customerById = customerService.getCustomerById(id);
        if (customerById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        CustomerDTO updateCustomer = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updateCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCustomer(@PathVariable UUID id) {
        CustomerDTO customerById = customerService.getCustomerById(id);
        if (customerById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        boolean deleted = customerService.deleteCustomer(id);
        return ResponseEntity.ok(deleted);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateCustomer(@RequestBody LoginDTO loginDTO) {
        CustomerDTO customerById = customerService.getCustomerByEmail(loginDTO.getUsername());
        if (customerById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_USERNAME)).build();
        }
        String token = customerService.authenticateSupplier(loginDTO);
        return token != null ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/delete-account")
    public ResponseEntity<Boolean> deleteAccount(@RequestBody LoginDTO loginDTO) {
        CustomerDTO customerById = customerService.getCustomerByEmail(loginDTO.getUsername());
        if (customerById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_USERNAME)).build();
        }
        Boolean deleted = customerService.deleteCustomer(customerById.getId());
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/{customerId}/carts")
    public ResponseEntity<List<CartDTO>> getCartsForSupplier(@PathVariable UUID customerId, @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "status", required = false) CartStatus status) {
        try {
            UUID customerById = getCustomerIdFromToken(authorizationHeader);
            if (!customerById.equals(customerId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")).build();
            }
            return ResponseEntity.ok(cartService.getCartsByCustomerId(customerId, status));


        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{customerId}/addAddress")
    public ResponseEntity<String> addAddress(@PathVariable UUID customerId, @RequestHeader("Authorization") String authorizationHeader, @RequestBody AddressDTO addressDTO) {
        try {
            UUID customerById = getCustomerIdFromToken(authorizationHeader);
            if (!customerById.equals(customerId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")).build();
            }
            addressDTO.setOwnerId(customerId);
            addressDTO.setOwnerType("CUSTOMER");
            customerService.addAddress(customerId, addressDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{customerId}/uploadProfilePicture")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable UUID customerId, @RequestHeader("Authorization") String authorizationHeader, @RequestBody ImageDTO profilePictureDTO) {
        try {
            UUID customerById = getCustomerIdFromToken(authorizationHeader);
            if (!customerById.equals(customerId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")).build();
            }
            customerService.uploadProfilePicture(customerId, profilePictureDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{customerId}/deleteAddress/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable UUID customerId, @RequestHeader("Authorization") String authorizationHeader, @PathVariable UUID addressId) {
        try {
            UUID customerById = getCustomerIdFromToken(authorizationHeader);
            if (!customerById.equals(customerId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")).build();
            }
            customerService.deleteAddress(customerId, addressId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    private UUID getCustomerIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        CustomerDTO customerDTO = customerService.getCustomerByEmail(username);
        if (customerDTO == null || !customerDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return customerDTO.getId();
    }

    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return token;
    }


}

