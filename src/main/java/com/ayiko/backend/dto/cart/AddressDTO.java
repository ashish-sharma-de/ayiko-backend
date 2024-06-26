package com.ayiko.backend.dto.cart;

import com.ayiko.backend.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private UUID id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private boolean isDefault;
    private String name;
    private String phoneNumber;

    private UUID ownerId;
    private String ownerType;
    private LocationDTO location;
}
