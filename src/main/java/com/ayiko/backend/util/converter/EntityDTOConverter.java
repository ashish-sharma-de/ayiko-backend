package com.ayiko.backend.util.converter;

import com.ayiko.backend.dto.*;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartItemDTO;
import com.ayiko.backend.repository.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDTOConverter {

    private static String LIMITER = "#@";
    public static String imageUrlsToString(List<String> imageUrls) {
        if(imageUrls == null || imageUrls.isEmpty()) {
            return "";
        }
        return String.join(LIMITER, imageUrls);
    }

    public static List<String> imageStringToList(String imageUrl) {
        if(imageUrl == null || imageUrl.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(imageUrl.split(LIMITER));
    }

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
                .businessImages(imageUrlsToString(supplierDTO.getBusinessImages()))
                .profileImageUrl(supplierDTO.getProfileImageUrl())
                .businessName(supplierDTO.getBusinessName())
                .businessDescription(supplierDTO.getBusinessDescription())
                .build();
    }

    public static SupplierDTO convertSupplierEntityToSupplierDTO(SupplierEntity supplierEntity) {
        return SupplierDTO.builder()
                .id(supplierEntity.getId())
                .bankAccountNumber(supplierEntity.getBankAccountNumber())
                .companyName(supplierEntity.getCompanyName())
                .emailAddress(supplierEntity.getEmailAddress())
                .ownerName(supplierEntity.getOwnerName())
                .password(supplierEntity.getPassword())
                .mobileMoneyNumber(supplierEntity.getMobileMoneyNumber())
                .city(supplierEntity.getCity())
                .phoneNumber(supplierEntity.getPhoneNumber())
                .businessImages(imageStringToList(supplierEntity.getBusinessImages()))
                .profileImageUrl(supplierEntity.getProfileImageUrl())
                .businessDescription(supplierEntity.getBusinessDescription())
                .businessName(supplierEntity.getBusinessName())
                .build();
    }

    public static ProductEntity convertProductDTOToEntity(ProductDTO productDTO) {
        return ProductEntity.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .unitPrice(productDTO.getUnitPrice())
                .quantity(productDTO.getQuantity())
                .imageUrl(imageUrlsToString(productDTO.getImageUrl()))
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
                .imageUrl(imageStringToList(entity.getImageUrl()))
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
