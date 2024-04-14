package com.ayiko.backend.service;

import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.LoginDTO;

import java.util.List;
import java.util.UUID;

public interface DriverService {
    DriverDTO createDriver(DriverDTO driverDTO);
    DriverDTO updateDriver(UUID id, DriverDTO driverDTO);
    boolean deleteDriver(UUID id);

    boolean deactivateDriver(UUID id);

    DriverDTO getDriverById(UUID id);
    List<DriverDTO> getAllDriversForSupplier(UUID id);

    DriverDTO getDriverByEmail(String email);

    boolean resetPassword(UUID id, String currentPassword, String newPassword);

    String authenticateDriver(LoginDTO loginDTO);
}
