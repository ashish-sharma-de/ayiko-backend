package com.ayiko.backend.service;

import com.ayiko.backend.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO updateProduct(UUID id, ProductDTO productDTO);
    boolean deleteProduct(UUID id);
    ProductDTO getProductById(UUID id);
    List<ProductDTO> getAllProductsForSupplier(UUID supplierId);

    List<ProductDTO> getPopularProducts();
}
