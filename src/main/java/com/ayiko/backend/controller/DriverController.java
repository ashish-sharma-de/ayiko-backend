package com.ayiko.backend.controller;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.service.DriverService;
import com.ayiko.backend.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/drivers")
public class DriverController {
    private final String ERROR_INVALID_TOKEN = "Token is empty or invalid, valid token required to access this API";
    private final String ERROR_DUPLICATE_EMAIL = "Email already present.";

    private final String ERROR_INVALID_USERNAME = "Username invalid.";

    private final String ERROR_INVALID_ID = "Specified customer id is invalid";

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired
    private DriverService driverService;

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO, @RequestHeader("Authorization") String supplierTokenHeader) {
        try {
            SupplierDTO supplierDTO = getSupplierIdFromToken(supplierTokenHeader);
            driverDTO.setSupplierId(supplierDTO.getId());
            DriverDTO driver = driverService.getDriverByEmail(driverDTO.getEmail());
            if (driver != null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ERROR_DUPLICATE_EMAIL)).build();
            }
            return ResponseEntity.ok(driverService.createDriver(driverDTO));
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    public List<DriverDTO> getAllDriver(@RequestHeader("Authorization") String supplierTokenHeader) {
        SupplierDTO supplierDTO = getSupplierIdFromToken(supplierTokenHeader);
        return driverService.getAllDriversForSupplier(supplierDTO.getId());
    }

    @GetMapping("/getByToken")
    public DriverDTO getDriverByToken(@RequestHeader("Authorization") String authorizationHeader) {
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
            return ExceptionHandler.handleException(e);
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
            return ExceptionHandler.handleException(e);
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
            return ExceptionHandler.handleException(e);
        }
    }


    @PostMapping("/{id}/deactivateDriver")
    public ResponseEntity<DriverDTO> deactivateDriver(@PathVariable UUID id,  @RequestHeader("Authorization") String supplierTokenHeader) {
        try {
            SupplierDTO supplierDTO = getSupplierIdFromToken(supplierTokenHeader);
            DriverDTO savedDriver = driverService.getDriverById(id);
            if(savedDriver.getSupplierId().equals(supplierDTO.getId()) == false){
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not authorized to deactivate this driver")).build();
            }
            driverService.deactivateDriver(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteDriver(@PathVariable UUID id) {
        try {


            DriverDTO driverById = driverService.getDriverById(id);
            if (driverById == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_ID)).build();
            }
            boolean deleted = driverService.deleteDriver(id);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateDriver(@RequestBody LoginDTO loginDTO) {
        try {
            DriverDTO driverByEmail = driverService.getDriverByEmail(loginDTO.getUsername());
            if (driverByEmail == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ERROR_INVALID_USERNAME)).build();
            }
            String token = driverService.authenticateDriver(loginDTO);
            return token != null ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
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

    private SupplierDTO getSupplierIdFromToken(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        String username = tokenProvider.getUsernameFromJWT(token);
        SupplierDTO supplierDTO = supplierService.getSupplierByEmail(username);
        if (supplierDTO == null || !supplierDTO.getEmailAddress().equals(username)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return supplierDTO;
    }

    private String validateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Assuming the header starts with "Bearer "
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException(ERROR_INVALID_TOKEN);
        }
        return token;
    }

}
