package com.ayiko.backend.service;

import com.ayiko.backend.dto.cart.CartDTO;
import com.ayiko.backend.dto.cart.CartPaymentConfirmationStatus;
import com.ayiko.backend.dto.cart.CartPaymentReceiptStatus;
import com.ayiko.backend.dto.cart.CartStatus;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartDTO saveCart(CartDTO cartDTO);

    CartDTO getCartById(UUID cartID);

    CartDTO updateCart(CartDTO cartDTO);

    boolean deleteCart(UUID cartId);

    List<CartDTO> getCartsByCustomerId(UUID customerId, CartStatus status);

    List<CartDTO> getCartsBySupplierId(UUID customerId, CartStatus status);

    void sendForApproval(UUID cartId);

    void acceptCart(UUID id);

    void rejectCart(UUID id);

    void updateCartPaymentConfirmationStatus(UUID id, CartPaymentConfirmationStatus status);

    void updateCartPaymentReceiptStatus(UUID id, CartPaymentReceiptStatus status);
}
