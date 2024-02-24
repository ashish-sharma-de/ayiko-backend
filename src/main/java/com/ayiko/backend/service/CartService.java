package com.ayiko.backend.service;

import com.ayiko.backend.dto.CartDTO;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartDTO saveCart(CartDTO cartDTO);

    CartDTO getCartById(UUID cartID);

    CartDTO updateCart(CartDTO cartDTO);

    boolean deleteCart(UUID cartId);

    List<CartDTO> getCartsByCustomerId(UUID customerId);

    List<CartDTO> getCartsBySupplierId(UUID customerId);
}
