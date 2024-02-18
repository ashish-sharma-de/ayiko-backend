package com.ayiko.backend.controller;

import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
        //TODO: add validation if the product is added by the same supplier as in the token
        return productService.addProduct(productDTO);
    }

    @GetMapping("supplier/{supplierId}")
    public List<ProductDTO> getAllProductsForSupplier(@PathVariable UUID supplierId) {
        return productService.getAllProductsForSupplier(supplierId);
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable UUID id) {
        //TODO: add validation if the product is get by the same supplier as in the token
        return productService.getProductById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestBody ProductDTO productDTO) {
        //TODO: add validation if the product is updated by the same supplier as in the token
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        //TODO: add validation if the product is deleted by the same supplier as in the token
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

