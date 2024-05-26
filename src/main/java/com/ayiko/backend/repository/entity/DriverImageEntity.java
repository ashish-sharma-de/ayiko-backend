package com.ayiko.backend.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverImageEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String imageUrl;
    private String imageType;
    private String imageTitle;
    private String imageDescription;

    @OneToOne
    @JoinColumn(name = "driver_id")
    private DriverEntity driver;
}
