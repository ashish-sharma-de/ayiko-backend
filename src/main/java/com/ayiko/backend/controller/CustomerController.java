package com.ayiko.backend.controller;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @PostMapping
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable UUID id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}/reset-password")
    public boolean resetPassword(@PathVariable UUID id, @RequestParam String currentPassword, @RequestParam String newPassword) {
        return customerService.resetPassword(id, currentPassword, newPassword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable UUID id, @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updateCustomer = customerService.updateCustomer(id, customerDTO);
        if (updateCustomer != null) {
            return ResponseEntity.ok(updateCustomer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateCustomer(@RequestBody LoginDTO loginDTO) {
        String token = customerService.authenticateSupplier(loginDTO);
        return token != null ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}

