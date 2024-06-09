package com.ayiko.backend.service.core;

import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartPaymentConfirmationStatus;
import com.ayiko.backend.dto.cart.CartPaymentReceiptStatus;
import com.ayiko.backend.dto.cart.CartStatus;
import com.ayiko.backend.repository.entity.CartEntity;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartDTO saveCart(CartDTO cartDTO);

    CartDTO getCartById(UUID cartID);

    CartDTO updateCart(CartDTO cartDTO);

    boolean canDelete(UUID cartId);

    boolean deleteCart(UUID cartId);

    List<CartDTO> getCartsByCustomerId(UUID customerId, CartStatus status);

    List<CartDTO> getCartsBySupplierId(UUID customerId, CartStatus status);

    void sendForApproval(UUID cartId);

    void acceptCart(UUID id);

    void rejectCart(UUID id);

    void updateCartPaymentConfirmationStatus(UUID id, CartPaymentConfirmationStatus status);

    CartEntity updateCartPaymentReceiptStatus(UUID id, CartPaymentReceiptStatus status);
}
