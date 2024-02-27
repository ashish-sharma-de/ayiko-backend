package com.ayiko.backend.util.converter;

import com.ayiko.backend.dto.*;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartItemDTO;
import com.ayiko.backend.repository.entity.*;

import java.util.List;
import java.util.stream.Collectors;

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
        return SupplierEntity.builder()
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
        return SupplierDTO.builder()
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

    public static CartDTO convertCartEntityToCartDTO(CartEntity entity) {
        List<CartItemDTO> items = entity.getItems().stream().map(item -> convertCartItemEntityToDTO(item)).collect(Collectors.toList());
        return CartDTO.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .supplierId(entity.getSupplierId())
                .items(items)
                .build();
    }

    public static CartItemDTO convertCartItemEntityToDTO(CartItemEntity itemEntity) {
        return CartItemDTO.builder()
                .id(itemEntity.getId())
                .productId(itemEntity.getProductId())
                .quantity(itemEntity.getQuantity())
                .build();
    }

    public static CartEntity convertCartDTOToEntity(CartDTO dto) {
        List<CartItemEntity> items = dto.getItems().stream().map(item -> convertCartItemDTOToEntity(item)).collect(Collectors.toList());
        return CartEntity.builder()
                .id(dto.getId())
                .customerId(dto.getCustomerId())
                .supplierId(dto.getSupplierId())
                .items(items)
                .build();
    }

    public static CartItemEntity convertCartItemDTOToEntity(CartItemDTO itemDTO) {
        return CartItemEntity.builder()
                .id(itemDTO.getId())
                .productId(itemDTO.getProductId())
                .quantity(itemDTO.getQuantity())
                .build();
    }

    public static DriverEntity convertDriverDTOToEntity(DriverDTO driverDTO) {
        return DriverEntity.builder()
                .id(driverDTO.getId())
                .email(driverDTO.getEmail())
                .password(driverDTO.getPassword())
                .phone(driverDTO.getPhone())
                .build();
    }

    public static DriverDTO convertDriverEntityToDTO(DriverEntity entity) {
        return DriverDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .phone(entity.getPhone())
                .build();
    }

}
