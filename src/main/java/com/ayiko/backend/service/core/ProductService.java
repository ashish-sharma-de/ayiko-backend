package com.ayiko.backend.service.core;

import com.ayiko.backend.dto.ImageDTO;
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
    boolean deleteProductImage(UUID id, UUID imageId);

    public boolean addProductImage(UUID id, ImageDTO imageDTO);

    List<ProductDTO> getBestSellingProductsByCategory(String category);

    List<ProductDTO> getAllProductsByCategoryId(String category);

    boolean checkCategory(String category);

    List<String> getAllCategoriesForSupplier(UUID id);

    List<ProductDTO> searchProductsForSupplier(UUID supplierId, String searchQuery);

    List<ProductDTO> searchProducts(String searchQuery);

    List<ProductDTO> filterProducts(UUID supplierId, String category, Boolean isAvailable, String priceMin, String priceMax, String[] sort);
}
