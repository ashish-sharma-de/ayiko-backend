package com.ayiko.backend.util.converter;

import com.ayiko.backend.dto.*;
import com.ayiko.backend.dto.cart.AddressDTO;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartItemDTO;
import com.ayiko.backend.dto.cart.PaymentDTO;
import com.ayiko.backend.dto.order.*;
import com.ayiko.backend.repository.entity.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EntityDTOConverter {

    private static String LIMITER = "#@";

    private static final GeometryFactory geometryFactory = new GeometryFactory();


    public static String imageUrlsToString(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return "";
        }
        return String.join(LIMITER, imageUrls);
    }

    public static List<String> imageStringToList(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
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
                .location(dto.getLocation() != null ?
                        geometryFactory.createPoint(
                                new Coordinate(dto.getLocation().getLongitude(),
                                        dto.getLocation().getLatitude()))
                        : null)
                .build();
    }

    public static CustomerDTO convertCustomerEntityToCustomerDTO(CustomerEntity entity) {
        if(entity == null) {
            return null;
        }
        CustomerDTO dto = CustomerDTO.builder()
                .id(entity.getId())
                .emailAddress(entity.getEmailAddress())
                .phoneNumber(entity.getPhoneNumber())
                .password(entity.getPassword())
                .fullName(entity.getFullName())
                .location(entity.getLocation() != null ?
                        LocationDTO.builder()
                                .latitude(entity.getLocation().getY())
                                .longitude(entity.getLocation().getX())
                                .build()
                        : null)
                .build();
        if(entity.getDeliveryAddresses() != null && entity.getDeliveryAddresses().size() > 0){
            for (AddressEntity addressEntity : entity.getDeliveryAddresses()) {
                if(dto.getDeliveryAddresses() == null){
                    dto.setDeliveryAddresses(new ArrayList<>());
                }
                dto.getDeliveryAddresses().add(EntityDTOConverter.convertAddressEntityToDTO(addressEntity));
            }
        }
        return dto;
    }

    public static SupplierEntity convertSupplierDTOToSupplierEntity(SupplierDTO supplierDTO) {
        SupplierEntity supplierEntity = SupplierEntity.builder()
                .id(supplierDTO.getId())
                .bankAccountNumber(supplierDTO.getBankAccountNumber())
                .companyName(supplierDTO.getCompanyName())
                .emailAddress(supplierDTO.getEmailAddress())
                .ownerName(supplierDTO.getOwnerName())
                .password(supplierDTO.getPassword())
                .mobileMoneyNumber(supplierDTO.getMobileMoneyNumber())
                .city(supplierDTO.getCity())
                .phoneNumber(supplierDTO.getPhoneNumber())
                .businessImageUrls(imageUrlsToString(supplierDTO.getBusinessImages()))
                .profileImageUrl(supplierDTO.getProfileImageUrl())
                .businessName(supplierDTO.getBusinessName())
                .businessDescription(supplierDTO.getBusinessDescription())
                .rating(supplierDTO.getRating())
                .location(supplierDTO.getLocation() != null ?
                        geometryFactory.createPoint(
                                new Coordinate(supplierDTO.getLocation().getLongitude(),
                                supplierDTO.getLocation().getLatitude()))
                        : null)
                .businessImages(supplierDTO.getImages() != null ? supplierDTO.getImages().stream().map(image ->
                        SupplierImageEntity.builder()
                                .imageUrl(image.getImageUrl())
                                .imageDescription(image.getImageDescription())
                                .imageTitle(image.getImageTitle())
                                .isProfilePicture(image.isProfilePicture())
                                .build())
                        .collect(Collectors.toSet())
                        : new HashSet<>())
                .build();

        supplierEntity.getBusinessImages().forEach(image -> image.setSupplier(supplierEntity));
        return supplierEntity;
    }

    public static SupplierDTO convertSupplierEntityToSupplierDTO(SupplierEntity supplierEntity) {
        if(supplierEntity == null) {
            return null;
        }
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
                .businessImages(imageStringToList(supplierEntity.getBusinessImageUrls()))
                .profileImageUrl(supplierEntity.getProfileImageUrl())
                .businessDescription(supplierEntity.getBusinessDescription())
                .businessName(supplierEntity.getBusinessName())
                .images(supplierEntity.getBusinessImages() != null ? supplierEntity.getBusinessImages().stream().map(image -> ImageDTO.builder()
                        .imageUrl(image.getImageUrl())
                        .imageDescription(image.getImageDescription())
                        .imageTitle(image.getImageTitle())
                        .isProfilePicture(image.isProfilePicture())
                        .build())
                        .collect(Collectors.toSet())
                        : new HashSet<>())
                .rating(supplierEntity.getRating())
                .location(supplierEntity.getLocation() != null ?
                        LocationDTO.builder()
                                .latitude(supplierEntity.getLocation().getY())
                                .longitude(supplierEntity.getLocation().getX())
                                .build()
                        : null)
                .build();
    }

    public static ProductEntity convertProductDTOToEntity(ProductDTO productDTO) {
        ProductEntity productEntity = ProductEntity.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .unitPrice(productDTO.getUnitPrice())
                .quantity(productDTO.getQuantity())
                .imageUrl(imageUrlsToString(productDTO.getImageUrlList()))
                .category(productDTO.getCategory())
                .productCategory(productDTO.getProductCategory())
                .isAvailable(productDTO.isAvailable())
                .images(productDTO.getImageUrl() != null ? productDTO.getImageUrl().stream().map(image -> ProductImageEntity.builder().imageTitle(image.getImageTitle())
                        .imageDescription(image.getImageDescription()).imageUrl(image.getImageUrl()).build()).collect(Collectors.toSet())
                        : new HashSet<>())
                .build();

        productEntity.getImages().forEach(image -> image.setProduct(productEntity));
        return productEntity;
    }

    public static ProductDTO convertProductEntityToDTO(ProductEntity entity) {
        if(entity == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .imageUrlList(imageStringToList(entity.getImageUrl()))
                .category(entity.getCategory())
                .supplierId(entity.getSupplier().getId())
                .isAvailable(entity.isAvailable())
                .productCategory(entity.getProductCategory())
                .imageUrl(entity.getImages().stream().map(image -> ImageDTO.builder().imageTitle(image.getImageTitle()).id(image.getId())
                        .imageDescription(image.getImageDescription()).imageUrl(image.getImageUrl()).build()).collect(Collectors.toSet()))
                .build();
    }

    public static CartDTO convertCartEntityToCartDTO(CartEntity entity) {
        Set<CartItemDTO> items = entity.getItems().stream().map(EntityDTOConverter::convertCartItemEntityToDTO).collect(Collectors.toSet());
        return CartDTO.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .supplierId(entity.getSupplierId())
                .status(entity.getStatus())
                .items(items)
                .orderId(entity.getOrder() != null ? entity.getOrder().getId() : null)
                .paymentDetails(PaymentDTO.builder()
                        .customerStatus(entity.getPaymentDetails() != null ? entity.getPaymentDetails().getConfirmationStatus() : null)
                        .supplierStatus(entity.getPaymentDetails() != null ? entity.getPaymentDetails().getReceiptStatus() : null)
                        .build())
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
        CartEntity entity = CartEntity.builder()
                .id(dto.getId())
                .customerId(dto.getCustomerId())
                .supplierId(dto.getSupplierId())
                .status(dto.getStatus())
                .deliveryAddressId(dto.getDeliveryAddress() != null ? dto.getDeliveryAddress().getId() : null)
                .build();
        Set<CartItemEntity> items = dto.getItems().stream().map(item -> convertCartItemDTOToEntity(item, entity)).collect(Collectors.toSet());
        entity.setItems(items);
        return entity;
    }

    public static CartItemEntity convertCartItemDTOToEntity(CartItemDTO itemDTO, CartEntity cartEntity) {
        return CartItemEntity.builder()
                .id(itemDTO.getId())
                .productId(itemDTO.getProductId())
                .quantity(itemDTO.getQuantity())
                .cart(cartEntity)
                .build();
    }

    public static DriverEntity convertDriverDTOToEntity(DriverDTO driverDTO) {
        return DriverEntity.builder()
                .id(driverDTO.getId())
                .email(driverDTO.getEmail())
                .password(driverDTO.getPassword())
                .phone(driverDTO.getPhone())
                .vehicleNumber(driverDTO.getVehicleNumber())
                .name(driverDTO.getName())
                .supplierId(driverDTO.getSupplierId())
                .isActive(driverDTO.isActive())
                .status(driverDTO.getStatus())
                .location(driverDTO.getLocation() != null ?
                        geometryFactory.createPoint(
                                new Coordinate(driverDTO.getLocation().getLongitude(),
                                        driverDTO.getLocation().getLatitude()))
                        : null)
                .build();
    }

    public static DriverDTO convertDriverEntityToDTO(DriverEntity entity) {
        if(entity == null) {
            return null;
        }
        return DriverDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .phone(entity.getPhone())
                .vehicleNumber(entity.getVehicleNumber())
                .name(entity.getName())
                .supplierId(entity.getSupplierId())
                .isActive(entity.isActive())
                .status(entity.getStatus())
                .location(entity.getLocation() != null ?
                        LocationDTO.builder()
                                .latitude(entity.getLocation().getY())
                                .longitude(entity.getLocation().getX())
                                .build()
                        : null)
                .build();
    }

    public static OrderEntity convertCartEntityToOrderEntity(CartEntity cartEntity) {

        OrderEntity orderEntity =  OrderEntity.builder()
                .customerId(cartEntity.getCustomerId())
                .supplierId(cartEntity.getSupplierId())
                .orderStatus(OrderStatus.CONFIRMED)
                .items(cartEntity.getItems().stream().map(item -> OrderItemEntity.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build()).collect(Collectors.toSet()))
                .paymentDetails(OrderPaymentEntity.builder()
                        .orderPaymentStatus(OrderPaymentStatus.PAID)
                        .paymentMethod(OrderPaymentMethod.OFFLINE)
                        .paymentDate(LocalDate.now())
                        .build())
                .build();
        orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        orderEntity.getPaymentDetails().setOrder(orderEntity);
        return orderEntity;
    }

    public static OrderDTO convertOrderEntityToDTO(OrderEntity orderEntity, CartDTO cart) {
        OrderDTO orderDTO = OrderDTO.builder()
                .id(orderEntity.getId())
                .customerId(orderEntity.getCustomerId())
                .supplierId(orderEntity.getSupplierId())
                .status(orderEntity.getOrderStatus())
                .items(orderEntity.getItems().stream().map(item -> OrderItemDTO.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build()).collect(Collectors.toSet()))
                .paymentDetails(OrderPaymentDTO.builder()
                        .paymentMethod(orderEntity.getPaymentDetails().getPaymentMethod())
                        .orderPaymentStatus(orderEntity.getPaymentDetails().getOrderPaymentStatus())
                        .paymentDate(orderEntity.getPaymentDetails().getPaymentDate())
                        .build())
                .driverId(orderEntity.getDriverId())
                .driverStatus(orderEntity.getDriverStatus())
                .isAssignedToSelf(false)
                .deliveryAddress(cart.getDeliveryAddress())
                .cart(cart)
                .build();
        if(orderEntity.getDriverId() != null && orderEntity.getSupplierId().equals(orderEntity.getDriverId())) {
            orderDTO.setAssignedToSelf(true);
        }
        return orderDTO;
    }

    public static AddressEntity convertAddressDTOToAddressEntity(AddressDTO addressDTO) {
        return AddressEntity.builder()
                .id(addressDTO.getId())
                .addressLine1(addressDTO.getAddressLine1())
                .addressLine2(addressDTO.getAddressLine2())
                .city(addressDTO.getCity())
                .postalCode(addressDTO.getPostalCode())
                .state(addressDTO.getState())
                .country(addressDTO.getCountry())
                .isDefault(addressDTO.isDefault())
                .ownerId(addressDTO.getOwnerId())
                .ownerType(addressDTO.getOwnerType())
                .name(addressDTO.getName())
                .phoneNumber(addressDTO.getPhoneNumber())
                .location(addressDTO.getLocation() != null ?
                    geometryFactory.createPoint(
                            new Coordinate(addressDTO.getLocation().getLongitude(),
                                    addressDTO.getLocation().getLatitude()))
                    : null)
                .build();
    }

    public static AddressDTO convertAddressEntityToDTO(AddressEntity entity) {
        return AddressDTO.builder()
                .id(entity.getId())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .postalCode(entity.getPostalCode())
                .state(entity.getState())
                .country(entity.getCountry())
                .isDefault(entity.isDefault())
                .ownerId(entity.getOwnerId())
                .ownerType(entity.getOwnerType())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .location(entity.getLocation() != null ?
                        LocationDTO.builder()
                                .latitude(entity.getLocation().getY())
                                .longitude(entity.getLocation().getX())
                                .build()
                        : null)
                .build();
    }

    public static SupplierRatingEntity convertSupplierRatingDTOtoEntity(SupplierRatingDTO supplierRatingDTO) {
        return SupplierRatingEntity.builder()
                .message(supplierRatingDTO.getMessage())
                .customerId(supplierRatingDTO.getCustomerId())
                .supplierId(supplierRatingDTO.getSupplierId())
                .rating(supplierRatingDTO.getRating())
                .build();
    }

    public static SupplierRatingDTO convertSupplierRatingEntityToDTO(SupplierRatingEntity entity) {
        return SupplierRatingDTO.builder()
                .message(entity.getMessage())
                .customerId(entity.getCustomerId())
                .supplierId(entity.getSupplierId())
                .rating(entity.getRating())
                .build();
    }
}
