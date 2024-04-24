package com.ayiko.backend.dto.order;

import com.ayiko.backend.dto.LocationDTO;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderTrackingDTO {
    private UUID orderId;
    private LocationDTO driverLocation;
}
