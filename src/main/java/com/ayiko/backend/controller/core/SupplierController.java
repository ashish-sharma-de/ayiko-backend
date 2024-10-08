package com.ayiko.backend.controller.core;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.*;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.service.core.CartService;
import com.ayiko.backend.service.core.ProductService;
import com.ayiko.backend.service.core.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
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
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        //TODO: Sort the suppliers by location
//        if (validateToken(authorizationHeader) == null) {
//            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ERROR_INVALID_TOKEN)).build();
//        }
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        if (tokenProvider.validateToken(token)) {
            return token;
        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplier(@PathVariable UUID id) {
        SupplierDTO supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        } else {
            supplierById.setProductCount((long) productService.getAllCategoriesForSupplier(id).size());
            return ResponseEntity.ok(supplierById);
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

    @PostMapping("/delete-account")
    public ResponseEntity<Boolean> deleteAccount(@RequestBody LoginDTO loginDTO) {
        SupplierDTO supplier = supplierService.getSupplierByEmail(loginDTO.getUsername());
        if (supplier == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_EMAIL)).build();
        }
        Boolean deleted = supplierService.deleteSupplier(supplier.getId());
        return ResponseEntity.ok(deleted);
    }


    @PostMapping("/{id}/addBusinessImage")
    public ResponseEntity<String> addBusinessImage(@PathVariable UUID id, @RequestBody ImageDTO imageDTO) {
        SupplierDTO supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        supplierService.addBusinessImage(id, imageDTO);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/deleteBusinessImage")
    public ResponseEntity<String> deleteBusinessImage(@PathVariable UUID id, @RequestBody ImageDTO imageDTO) {
        SupplierDTO supplierById = supplierService.getSupplierById(id);
        if (supplierById == null) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
        }
        supplierService.deleteBusinessImage(id, imageDTO);
        return ResponseEntity.ok().build();
    }

    private UUID getSupplierIdFromToken(String token) {
        String username = tokenProvider.getUsernameFromJWT(token);
        SupplierDTO supplierDTO = supplierService.getSupplierByEmail(username);
        if (supplierDTO == null || !supplierDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_EMAIL);
        }
        return supplierDTO.getId();
    }

    @GetMapping("/{supplierId}/carts")
    public ResponseEntity<List<CartDTO>> getCartsForSupplier(@PathVariable UUID supplierId, @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "status", required = false) CartStatus status) {
        try {
            String token = validateToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ERROR_INVALID_TOKEN)).build();
            }
            UUID supplierIdFromToken = getSupplierIdFromToken(token);
            if (!supplierIdFromToken.equals(supplierId)) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")).build();
            }
            return ResponseEntity.ok(cartService.getCartsBySupplierId(supplierId, status));

        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("{id}/products")
    public ResponseEntity<List<ProductDTO>> getAllProductsForSupplier(@PathVariable UUID id) {
        return getListResponseEntity(id);
    }

    @GetMapping("{id}/categories")
    public ResponseEntity<List<String>> getCategoriesForSupplier(@PathVariable UUID id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = validateToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ERROR_INVALID_TOKEN)).build();
            }
            return ResponseEntity.ok(productService.getAllCategoriesForSupplier(id));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/byCategory/{category}")
    public ResponseEntity<Set<SupplierDTO>> getSuppliersByCategory(@PathVariable String category) {
        try {
            List<ProductDTO> productDTOS = productService.getAllProductsByCategoryId(category);
            Set<SupplierDTO> supplierDTOList = productDTOS.stream().map(ProductDTO::getSupplierDTO).collect(Collectors.toSet());
            return ResponseEntity.ok(supplierDTOList);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("{id}/bestsellers")
    public ResponseEntity<List<ProductDTO>> getBestsellersForSupplier(@PathVariable UUID id) {
        return getListResponseEntity(id);
    }

    private ResponseEntity<List<ProductDTO>> getListResponseEntity(UUID id) {
        try {
//            String token = validateToken(authorizationHeader);
//            if (token == null) {
//                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ERROR_INVALID_TOKEN)).build();
//            }
            return ResponseEntity.ok(productService.getAllProductsForSupplier(id));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/getByToken")
    public SupplierDTO getSupplierByToken(@RequestHeader("Authorization") String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        UUID supplierIdFromToken = getSupplierIdFromToken(token);
        return supplierService.getSupplierById(supplierIdFromToken);
    }

    @GetMapping("/search")
    public HashMap<String, List> searchSupplier(@RequestParam String searchQuery) {
        List<SupplierDTO> suppliers = supplierService.searchSupplier(searchQuery);
        List<ProductDTO> products = productService.searchProducts(searchQuery);
        HashMap<String, List> result = new HashMap<String, List>();
        result.put("items", products);
        result.put("suppliers", suppliers);
        return  result;
    }

    @GetMapping("/nearBy")
    public List<SupplierDTO> getNearBySuppliers(@RequestParam double lat, @RequestParam double lon, @RequestParam double distance) {
        return supplierService.findNearbySuppliers(lat, lon, distance);
    }
}

