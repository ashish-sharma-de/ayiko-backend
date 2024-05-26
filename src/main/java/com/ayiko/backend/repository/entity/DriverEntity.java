package com.ayiko.backend.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String vehicleNumber;
    private String password;
    private boolean isActive;
    private UUID supplierId;
    private DriverStatus status;

    @LastModifiedDate
    private LocalDate updatedAt;
    private LocalDate lastLoginAt;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DriverImageEntity profileImage;

    private Point location;

}
