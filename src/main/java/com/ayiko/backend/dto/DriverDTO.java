package com.ayiko.backend.dto;

import com.ayiko.backend.repository.entity.DriverStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class DriverDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String vehicleNumber;
    private String password;
    private UUID supplierId;
    private boolean isActive;
    private DriverStatus status;

    private LocationDTO location;

    @LastModifiedDate
    private LocalDate updatedAt;
    private LocalDate lastLoginAt;

    private ImageDTO profileImage;
}
