package com.ayiko.backend.controller;

import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.service.ProductService;
import com.ayiko.backend.service.SupplierService;
import com.ayiko.backend.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    private JWTUtil jwtUtil = new JWTUtil();

    @PostMapping
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO, @RequestHeader("Authorization") String authorizationHeader) {
        UUID supplierId = validateToken(authorizationHeader);
        productDTO.setSupplierId(supplierId);
        return productService.addProduct(productDTO);
    }

    private UUID validateToken(String authorizationHeader) {
        //TODO: Update logic if required to check if the token is valid
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if(!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        String username = jwtUtil.getUsernameFromJWT(token);
        SupplierDTO supplierDTO = supplierService.getSupplierByEmail(username);
        if(supplierDTO == null || !supplierDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException("Token userId doesn't match the supplierId in product");
        }
        return supplierDTO.getId();
    }

    @GetMapping
    public List<ProductDTO> getAllProductsForSupplier(@RequestHeader("Authorization") String authorizationHeader) {
        UUID supplierId = validateToken(authorizationHeader);
        return productService.getAllProductsForSupplier(supplierId);
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        validateToken(authorizationHeader);
        return productService.getProductById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @RequestBody ProductDTO productDTO) {
        UUID supplierId = validateToken(authorizationHeader);
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        updatedProduct.setSupplierId(supplierId);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/supplier/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        validateToken(authorizationHeader);
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

