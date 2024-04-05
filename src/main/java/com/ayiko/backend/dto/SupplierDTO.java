package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class SupplierDTO {
    private UUID id;
    private String companyName;
    private String ownerName;
    private String phoneNumber;
    private String mobileMoneyNumber;
    private String bankAccountNumber;
    private String city;
    private String emailAddress;
    private String password; // Note: handle the password securely, consider not exposing in DTO

    private String profileImageUrl;
    private List<String> businessImages;
    private Set<ImageDTO> images;
    private String businessName;
    private String businessDescription;
    private Double rating;
    private LocationDTO location;
}
