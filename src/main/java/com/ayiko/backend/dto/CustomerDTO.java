package com.ayiko.backend.dto;

import com.ayiko.backend.dto.cart.AddressDTO;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CustomerDTO {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String emailAddress;
    private String password; // Note: handle the password securely, consider not exposing in DTO
    private LocationDTO location;
    private List<AddressDTO> deliveryAddresses = new ArrayList<>();
}
