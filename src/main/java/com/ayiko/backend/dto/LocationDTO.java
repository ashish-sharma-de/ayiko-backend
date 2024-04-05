package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDTO {
    private Double latitude;
    private Double longitude;
}
