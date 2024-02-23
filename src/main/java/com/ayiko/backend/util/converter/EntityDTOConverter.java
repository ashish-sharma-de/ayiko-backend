package com.ayiko.backend.util.converter;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.dto.SupplierDTO;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.ProductEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;

public class EntityDTOConverter {

    public static CustomerEntity convertCustomerDTOToCustomerEntity(CustomerDTO dto) {
        return CustomerEntity.builder()
                //.id(dto.getId())
                .emailAddress(dto.getEmailAddress())
                .phoneNumber(dto.getPhoneNumber())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .build();
    }

    public static CustomerDTO convertCustomerEntityToCustomerDTO(CustomerEntity entity) {
        return CustomerDTO.builder()
                .id(entity.getId())
                .emailAddress(entity.getEmailAddress())
                .phoneNumber(entity.getPhoneNumber())
                .password(entity.getPassword())
                .fullName(entity.getFullName())
                .build();
    }
    public static SupplierEntity convertSupplierDTOToSupplierEntity(SupplierDTO supplierDTO) {
        return  SupplierEntity.builder()
                .id(supplierDTO.getId())
                .bankAccountNumber(supplierDTO.getBankAccountNumber())
                .companyName(supplierDTO.getCompanyName())
                .emailAddress(supplierDTO.getEmailAddress())
                .ownerName(supplierDTO.getOwnerName())
                .password(supplierDTO.getPassword())
                .mobileMoneyNumber(supplierDTO.getMobileMoneyNumber())
                .city(supplierDTO.getCity())
                .phoneNumber(supplierDTO.getPhoneNumber())
                .build();
    }

    public static SupplierDTO convertSupplierEntityToSupplierDTO(SupplierEntity dto) {
        return  SupplierDTO.builder()
                .id(dto.getId())
                .bankAccountNumber(dto.getBankAccountNumber())
                .companyName(dto.getCompanyName())
                .emailAddress(dto.getEmailAddress())
                .ownerName(dto.getOwnerName())
                .password(dto.getPassword())
                .mobileMoneyNumber(dto.getMobileMoneyNumber())
                .city(dto.getCity())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }

    public static ProductEntity convertProductDTOToEntity(ProductDTO productDTO) {
        return ProductEntity.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .unitPrice(productDTO.getUnitPrice())
                .quantity(productDTO.getQuantity())
                .imageUrl(productDTO.getImageUrl())
                .category(productDTO.getCategory())
                .isAvailable(productDTO.isAvailable())
                .build();
    }

    public static ProductDTO convertProductEntityToDTO(ProductEntity entity) {
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .imageUrl(entity.getImageUrl())
                .category(entity.getCategory())
                .supplierId(entity.getSupplier().getId())
                .isAvailable(entity.isAvailable())
                .build();
    }
}
