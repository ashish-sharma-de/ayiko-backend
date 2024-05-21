package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.service.ProductService;
import com.ayiko.backend.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
    @Autowired
    private JWTTokenProvider tokenProvider;
    private final String ERROR_INVALID_TOKEN = "Token invalid, valid token required to access this API";
    private final String ERROR_INVALID_PRODUCT_ID = "Token invalid, valid token required to access this API";

    private final String ERROR_INVALID_SUPPLIER_ID = "Supplier id doesn't match the supplierId in token or product";

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            logger.info("Adding product", productDTO);
            UUID supplierId = getSupplierIdFromToken(authorizationHeader);
            supplierService.getSupplierById(supplierId);
            productDTO.setSupplierId(supplierId);
            ProductDTO savedProduct = productService.addProduct(productDTO);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    private UUID getSupplierIdFromToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        String username = tokenProvider.getUsernameFromJWT(token);
        SupplierDTO supplierDTO = supplierService.getSupplierByEmail(username);
        if (supplierDTO == null || !supplierDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_SUPPLIER_ID);
        }
        return supplierDTO.getId();
    }

    private void validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts(@RequestHeader("Authorization") String authorizationHeader) {

        try {
            validateToken(authorizationHeader);
            return ResponseEntity.ok(productService.getPopularProducts());
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            getSupplierIdFromToken(authorizationHeader);
            ProductDTO productById = productService.getProductById(id);
            if (productById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_PRODUCT_ID)).build();
            }
            return ResponseEntity.ok(productById);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @RequestBody ProductDTO productDTO) {
        try {
            UUID supplierId = getSupplierIdFromToken(authorizationHeader);
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            updatedProduct.setSupplierId(supplierId);
            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_PRODUCT_ID)).build();
            }
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            getSupplierIdFromToken(authorizationHeader);
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}/deleteImage/{imageId}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @PathVariable UUID imageId) {
        try {
            getSupplierIdFromToken(authorizationHeader);
            boolean deleted = productService.deleteProductImage(id, imageId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/{id}/addImage")
    public ResponseEntity<Void> addProductImage(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader, @RequestBody ImageDTO imageDTO) {
        try {
            getSupplierIdFromToken(authorizationHeader);
            boolean isAdded = productService.addProductImage(id, imageDTO);
            if (isAdded) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{category}/bestSelling")
    public ResponseEntity<List<ProductDTO>> getBestSellingProducts(@PathVariable String category, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            validateToken(authorizationHeader);
            boolean validCategory = productService.checkCategory(category);
            if (!validCategory) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Invalid category")).build();
            }
            List<ProductDTO> bestSellingProducts = productService.getBestSellingProductsByCategory(category);
            return ResponseEntity.ok(bestSellingProducts);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

}

