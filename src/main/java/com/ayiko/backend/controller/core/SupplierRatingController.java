package com.ayiko.backend.controller.core;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.SupplierRatingDTO;
import com.ayiko.backend.service.core.CustomerService;
import com.ayiko.backend.service.core.SupplierRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/supplier/rating")
public class SupplierRatingController {
    @Autowired
    SupplierRatingService ratingService;

    @Autowired
    CustomerService customerService;

    @Autowired
    private JWTTokenProvider tokenProvider;

    private final String ERROR_INVALID_TOKEN = "Token is empty or invalid, valid token required to access this API";

    @PostMapping
    public ResponseEntity addRatingForSupplier(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SupplierRatingDTO ratingDTO) {
        validateToken(authorizationHeader);
        String username = getUsernameFromToken(authorizationHeader.substring(7));
        CustomerDTO customerByEmail = customerService.getCustomerByEmail(username);
        if(customerByEmail == null) {
            throw new RuntimeException("Customer not found");
        }
        if(!customerByEmail.getId().equals(ratingDTO.getCustomerId())) {
            throw new RuntimeException("Customer id in token does not match the customer id in request body");
        }
        ratingService.addSupplierRating(ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    private String getUsernameFromToken(String token) {
        return tokenProvider.getUsernameFromJWT(token);
    }
    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return token;
    }

}
