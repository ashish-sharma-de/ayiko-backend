package com.ayiko.backend.controller;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.service.CustomerService;
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
    private CustomerService customerService;


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

}

