package com.ayiko.backend.service.impl;

import com.ayiko.backend.dto.CartDTO;
import com.ayiko.backend.repository.CartRepository;
import com.ayiko.backend.repository.entity.CartEntity;
import com.ayiko.backend.service.CartService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartDTO saveCart(CartDTO cartDTO) {
        CartEntity cartEntity = EntityDTOConverter.convertCartDTOToEntity(cartDTO);
        CartEntity save = cartRepository.save(cartEntity);
        return EntityDTOConverter.convertCartEntityToCartDTO(save);
    }

    @Override
    public CartDTO getCartById(UUID cartID) {
        return cartRepository.findById(cartID).map(EntityDTOConverter::convertCartEntityToCartDTO).orElse(null);
    }

    @Override
    public CartDTO updateCart(CartDTO cartDTO) {
        CartEntity cartEntity = cartRepository.findById(cartDTO.getId()).orElse(null);
        if (cartEntity != null) {
            CartEntity save = cartRepository.save(EntityDTOConverter.convertCartDTOToEntity(cartDTO));
            return EntityDTOConverter.convertCartEntityToCartDTO(save);
        }
        return null;
    }

    @Override
    public boolean deleteCart(UUID cartId) {
        boolean exists = cartRepository.existsById(cartId);
        if (exists) {
            cartRepository.deleteById(cartId);
            return true;
        }
        return false;
    }

    @Override
    public List<CartDTO> getCartsByCustomerId(UUID customerId) {
        return cartRepository.findAllByCustomerId(customerId).stream().map(EntityDTOConverter::convertCartEntityToCartDTO).toList();
    }

    @Override
    public List<CartDTO> getCartsBySupplierId(UUID customerId) {
        return cartRepository.findAllBySupplierId(customerId).stream().map(EntityDTOConverter::convertCartEntityToCartDTO).toList();
    }

}
