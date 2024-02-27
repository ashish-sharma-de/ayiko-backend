package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.service.CartService;
import com.ayiko.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/drivers")
public class DriverEntityController {
    private final String ERROR_INVALID_TOKEN = "Token is empty or invalid, valid token required to access this API";
    private final String ERROR_DUPLICATE_EMAIL = "Email already present.";

    private final String ERROR_INVALID_USERNAME = "Username invalid.";

    private final String ERROR_INVALID_ID = "Specified customer id is invalid";

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired
    private DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO customerDTO) {
        try {
            DriverDTO driver = driverService.getDriverByEmail(customerDTO.getEmail());
            if (driver != null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ERROR_DUPLICATE_EMAIL)).build();
            }
            return ResponseEntity.ok(driverService.createDriver(customerDTO));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping
    public List<DriverDTO> getAllDriver() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/getByToken")
    public DriverDTO getCustomerByToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {

            UUID customerIdFromToken = getDriverIdFromToken(authorizationHeader);
            return driverService.getDriverById(customerIdFromToken);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable UUID id) {
        try {
            DriverDTO driverById = driverService.getDriverById(id);
            if (driverById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
            } else {
                return ResponseEntity.ok(driverById);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Boolean> resetPassword(@PathVariable UUID id, @RequestParam String currentPassword, @RequestParam String newPassword) {
        try {
            DriverDTO driverById = driverService.getDriverById(id);
            if (driverById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
            }
            return ResponseEntity.ok(driverService.resetPassword(id, currentPassword, newPassword));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable UUID id, @RequestBody DriverDTO driverDTO) {
        try {
            DriverDTO driverById = driverService.updateDriver(id, driverDTO);
            if (driverById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
            }
            DriverDTO updateDriver = driverService.updateDriver(id, driverDTO);
            return ResponseEntity.ok(updateDriver);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCustomer(@PathVariable UUID id) {
        try {


            DriverDTO driverById = driverService.getDriverById(id);
            if (driverById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
            }
            boolean deleted = driverService.deleteDriver(id);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateCustomer(@RequestBody LoginDTO loginDTO) {
        try {
            DriverDTO driverByEmail = driverService.getDriverByEmail(loginDTO.getUsername());
            if (driverByEmail == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_USERNAME)).build();
            }
            String token = driverService.authenticateDriver(loginDTO);
            return token != null ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }


    private ResponseEntity handleException(Exception e) {
        if (e instanceof RuntimeException && (e.getMessage().equals(ERROR_INVALID_TOKEN) || e.getMessage().equals(ERROR_INVALID_ID))) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage())).build();
        }
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())).build();
    }

    private UUID getDriverIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        DriverDTO driver = driverService.getDriverByEmail(username);
        if (driver == null || !driver.getEmail().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return driver.getId();
    }

    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return token;
    }

}
