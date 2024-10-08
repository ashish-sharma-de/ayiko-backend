package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartPaymentConfirmationStatus;
import com.ayiko.backend.dto.cart.CartPaymentReceiptStatus;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.repository.core.*;
import com.ayiko.backend.repository.core.entity.*;
import com.ayiko.backend.service.core.CartService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private CartDTO convertCartEntityToCartDTO(CartEntity entity){
        CartDTO dto = EntityDTOConverter.convertCartEntityToCartDTO(entity);
        dto.getItems().forEach(cartItemDTO -> {
            if(cartItemDTO.getProductId() == null){
                System.out.println(cartItemDTO);
            }
            ProductEntity byId = productRepository.findById(cartItemDTO.getProductId()).orElse(null);
            ProductDTO productDTO = byId != null ? EntityDTOConverter.convertProductEntityToDTO(byId) : null;
            cartItemDTO.setProduct(productDTO);
        });
        if(entity.getDeliveryAddressId() != null){
            addressRepository.findById(entity.getDeliveryAddressId()).ifPresent(addressEntity -> dto.setDeliveryAddress(EntityDTOConverter.convertAddressEntityToDTO(addressEntity)));
        }
        SupplierEntity supplierEntity = supplierRepository.findById(dto.getSupplierId()).orElse(null);
        dto.setSupplier(EntityDTOConverter.convertSupplierEntityToSupplierDTO(supplierEntity));
        dto.setCustomer(EntityDTOConverter.convertCustomerEntityToCustomerDTO(customerRepository.findById(dto.getCustomerId()).orElse(null)));
        return dto;
    }

    @Override
    public CartDTO saveCart(CartDTO cartDTO) {
        CartEntity cartEntity = EntityDTOConverter.convertCartDTOToEntity(cartDTO);
        CartEntity save = cartRepository.save(cartEntity);
        return convertCartEntityToCartDTO(save);
    }

    @Override
    public CartDTO getCartById(UUID cartID) {
        Optional<CartEntity> byId = cartRepository.findById(cartID);
        if (byId.isEmpty()) {
            return null;
        }
        CartEntity cartEntity = byId.get();
        cartEntity.getItems();
        return convertCartEntityToCartDTO(cartEntity);
    }

    @Override
    public CartDTO updateCart(CartDTO cartDTO) {
        CartEntity cartEntity = cartRepository.findById(cartDTO.getId()).orElse(null);
        if (cartEntity != null) {
            cartEntity.setSupplierId(cartDTO.getSupplierId() == null ? cartEntity.getSupplierId() : cartDTO.getSupplierId());
            cartEntity.setCustomerId(cartDTO.getCustomerId() == null ? cartEntity.getCustomerId() : cartDTO.getCustomerId());
            cartEntity.setStatus(cartDTO.getStatus() == null ? cartEntity.getStatus() : cartDTO.getStatus());
            cartEntity.setUpdatedAt(LocalDate.now());
            cartDTO.getItems().forEach(cartItemDTO -> {
                CartItemEntity cartItemEntity = EntityDTOConverter.convertCartItemDTOToEntity(cartItemDTO, cartEntity);
                cartEntity.getItems().stream().filter(item -> item.getId().equals(cartItemDTO.getId())).findFirst().ifPresent(cartItemEntity1 -> {
                    cartEntity.getItems().remove(cartItemEntity1);
                });
                cartEntity.addItem(cartItemEntity);
            });
            CartEntity save = cartRepository.save(cartEntity);
            return convertCartEntityToCartDTO(save);
        }
        return null;
    }
    @Override
    public boolean canDelete(UUID cartId) {
        Optional<CartEntity> cartEntityOptional = cartRepository.findById(cartId);
        if(cartEntityOptional.isPresent()){
            CartEntity entity = cartEntityOptional.get();
            if(entity.getOrder() != null){
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean deleteCart(UUID cartId) {
        Optional<CartEntity> cartEntityOptional = cartRepository.findById(cartId);
        if(cartEntityOptional.isPresent()){
            CartEntity entity = cartEntityOptional.get();
            if(entity.getOrder() != null){
                return false;
            }
            cartRepository.delete(entity);
            return true;
        }
        return false;
    }

    @Override
    public List<CartDTO> getCartsByCustomerId(UUID customerId, CartStatus status) {
        if (status != null) {
            return cartRepository.findAllByCustomerIdAndStatus(customerId, status).stream().map(this::convertCartEntityToCartDTO).toList();
        }
        return cartRepository.findAllByCustomerId(customerId).stream().map(this::convertCartEntityToCartDTO).toList();
    }

    @Override
    public List<CartDTO> getCartsBySupplierId(UUID customerId, CartStatus status) {
        if (status != null) {
            return cartRepository.findAllBySupplierIdAndStatus(customerId, status).stream().map(this::convertCartEntityToCartDTO).toList();
        }
        return cartRepository.findAllBySupplierId(customerId).stream().map(this::convertCartEntityToCartDTO).toList();
    }

    @Override
    public void sendForApproval(UUID id) {
        CartEntity cartEntity = cartRepository.findById(id).orElse(null);
        if (cartEntity == null) {
            throw new RuntimeException("Invalid Cart ID");
        }
        cartEntity.setStatus(CartStatus.SENT_FOR_APPROVAL);
        cartRepository.save(cartEntity);
    }

    @Override
    public void acceptCart(UUID id) {
        CartEntity cartEntity = cartRepository.findById(id).orElse(null);
        if (cartEntity == null) {
            throw new RuntimeException("Invalid Cart ID");
        }
        cartEntity.setStatus(CartStatus.ACCEPTED);
        cartRepository.save(cartEntity);
    }

    @Override
    public void rejectCart(UUID id) {
        CartEntity cartEntity = cartRepository.findById(id).orElse(null);
        if (cartEntity == null) {
            throw new RuntimeException("Invalid Cart ID");
        }
        cartEntity.setStatus(CartStatus.REJECTED);
        cartRepository.save(cartEntity);
    }

    @Override
    public void updateCartPaymentConfirmationStatus(UUID id, CartPaymentConfirmationStatus status) {
        CartEntity cartEntity = cartRepository.findById(id).orElse(null);
        if (cartEntity == null) {
            throw new RuntimeException("Invalid Cart ID");
        }
        CartPaymentEntity paymentDetails = cartEntity.getPaymentDetails();
        if (paymentDetails == null) {
            paymentDetails = CartPaymentEntity.builder().build();
        }
        paymentDetails.setConfirmationStatus(status);
        paymentDetails.setConfirmationDate(LocalDate.now());
        paymentDetails.setCart(cartEntity);
        cartEntity.setPaymentDetails(paymentDetails);
        cartRepository.save(cartEntity);
    }

    @Override
    public CartEntity updateCartPaymentReceiptStatus(UUID id, CartPaymentReceiptStatus status) {
        CartEntity cartEntity = cartRepository.findById(id).orElse(null);
        if (cartEntity == null) {
            throw new RuntimeException("Invalid Cart ID");
        }
        CartPaymentEntity paymentDetails = cartEntity.getPaymentDetails();
        if (paymentDetails == null) {
            paymentDetails = CartPaymentEntity.builder().build();
        }
        paymentDetails.setReceiptStatus(status);
        paymentDetails.setReceiptDate(LocalDate.now());
        paymentDetails.setCart(cartEntity);
        cartEntity.setPaymentDetails(paymentDetails);
        return cartRepository.save(cartEntity);
    }


}
