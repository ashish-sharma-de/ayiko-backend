package com.ayiko.backend.controller;

import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public SupplierDTO createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return supplierService.createSupplier(supplierDTO);
    }

    @GetMapping
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public SupplierDTO getSupplier(@PathVariable UUID id) {
        return supplierService.getSupplierById(id);
    }

    @PutMapping("/{id}/reset-password")
    public boolean resetPassword(@PathVariable UUID id, @RequestParam String currentPassword, @RequestParam String newPassword) {
        return supplierService.resetPassword(id, currentPassword, newPassword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable UUID id, @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        if (updatedSupplier != null) {
            return ResponseEntity.ok(updatedSupplier);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        boolean deleted = supplierService.deleteSupplier(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/authenticate/")
    public ResponseEntity<Void> authenticateSupplier(@RequestBody UUID id) {
        boolean deleted = supplierService.deleteSupplier(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

