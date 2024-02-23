package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private JWTTokenProvider tokenProvider;
    private final String ERROR_INVALID_TOKEN = "Token is empty or invalid, valid token required to access this API";
    private final String ERROR_DUPLICATE_EMAIL = "Email already present.";
    private final String ERROR_INVALID_EMAIL = "Email invalid.";
    private final String ERROR_INVALID_ID = "Specified supplier id is invalid";

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
        if (supplierService.getSupplierByEmail(supplierDTO.getEmailAddress()) != null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ERROR_DUPLICATE_EMAIL)).build();
        }
        SupplierDTO supplier = supplierService.createSupplier(supplierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(@RequestHeader("Authorization") String authorizationHeader) {
        //TODO: Sort the suppliers by location
        if (!validateToken(authorizationHeader)) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ERROR_INVALID_TOKEN)).build();
        }
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    private boolean validateToken(String authorizationHeader) {
        if (tokenProvider.validateToken(authorizationHeader.substring(7))) {
            return true;
        }
        return false;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplier(@PathVariable UUID id) {
        SupplierDTO supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Boolean> resetPassword(@PathVariable UUID id, @RequestParam String currentPassword, @RequestParam String newPassword) {
        SupplierDTO supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        boolean result = supplierService.resetPassword(id, currentPassword, newPassword);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable UUID id, @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        boolean deleted = supplierService.deleteSupplier(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateSupplier(@RequestBody LoginDTO loginDTO) {
        SupplierDTO supplierById = supplierService.getSupplierByEmail(loginDTO.getUsername());
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_EMAIL)).build();
        }
        String token = supplierService.authenticateSupplier(loginDTO);
        return token != null ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

