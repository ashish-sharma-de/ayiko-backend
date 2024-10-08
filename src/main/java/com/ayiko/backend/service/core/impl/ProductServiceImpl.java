package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.repository.core.ProductEntitySpecification;
import com.ayiko.backend.repository.core.ProductRepository;
import com.ayiko.backend.repository.core.SupplierRepository;
import com.ayiko.backend.repository.core.entity.ProductEntity;
import com.ayiko.backend.repository.core.entity.ProductImageEntity;
import com.ayiko.backend.repository.core.entity.SupplierEntity;
import com.ayiko.backend.service.core.ProductService;
import com.ayiko.backend.service.core.SupplierService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        SupplierEntity supplierEntity = supplierRepository.findById(productDTO.getSupplierId()).orElseThrow(() -> new RuntimeException("Supplier not found"));
        ProductEntity productEntity = EntityDTOConverter.convertProductDTOToEntity(productDTO);
        productEntity.setSupplier(supplierEntity);
        ProductEntity save = productRepository.save(productEntity);
        return EntityDTOConverter.convertProductEntityToDTO(save);
    }

    @Override
    public ProductDTO updateProduct(UUID id, ProductDTO productDTO) {
        Optional<ProductEntity> byId = productRepository.findById(id);
        if (byId.isPresent()) {
            ProductEntity productEntity = byId.get();
            productEntity.setName(productDTO.getName() != null ? productDTO.getName() : productEntity.getName());
            productEntity.setUnitPrice(productDTO.getUnitPrice() != null ? productDTO.getUnitPrice() : productEntity.getUnitPrice());
            productEntity.setQuantity(productDTO.getQuantity() != null ? productDTO.getQuantity() : productEntity.getQuantity());
            productEntity.setCategory(productDTO.getCategory() != null ? productDTO.getCategory() : productEntity.getCategory());
            productEntity.setDescription(productDTO.getDescription() != null ? productDTO.getDescription() : productEntity.getDescription());
            productEntity.setImageUrl(productDTO.getImageUrlList() != null ? EntityDTOConverter.imageUrlsToString(productDTO.getImageUrlList()) : productEntity.getImageUrl());
            productEntity.setAvailable(productDTO.isAvailable());
            if(productDTO.getImageUrl() != null){
                productDTO.getImageUrl().forEach(imageDTO -> {
                    ProductImageEntity productImageEntity = ProductImageEntity.builder()
                            .imageUrl(imageDTO.getImageUrl())
                            .imageType(imageDTO.getImageType())
                            .imageTitle(imageDTO.getImageTitle())
                            .imageDescription(imageDTO.getImageDescription())
                            .product(productEntity)
                            .build();
                    productEntity.getImages().add(productImageEntity);
                });
            }
            ProductEntity save = productRepository.save(productEntity);
            return EntityDTOConverter.convertProductEntityToDTO(save);
        }
        return null;
    }

    @Override
    public boolean deleteProduct(UUID id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        return productRepository.findById(id).map(EntityDTOConverter::convertProductEntityToDTO).orElse(null);
    }

    @Override
    public List<ProductDTO> getAllProductsForSupplier(UUID supplierId) {
        return productRepository.findBySupplierId(supplierId).stream().map(EntityDTOConverter::convertProductEntityToDTO).toList();
    }

    @Override
    public List<ProductDTO> getPopularProducts() {
        return productRepository.findAll().stream().map(EntityDTOConverter::convertProductEntityToDTO).toList();
    }

    @Override
    public boolean deleteProductImage(UUID id, UUID imageId) {
        Optional<ProductEntity> byId = productRepository.findById(id);
        if (byId.isPresent()) {
            ProductEntity productEntity = byId.get();
            Set<ProductImageEntity> images = productEntity.getImages();
            Optional<ProductImageEntity> imageEntity = images.stream().filter(productImageEntity -> productImageEntity.getId().equals(imageId)).findFirst();
            if(imageEntity.isPresent()){
                images.remove(imageEntity.get());
                productEntity.setImages(images);
                productRepository.save(productEntity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addProductImage(UUID id, ImageDTO imageDTO) {
        Optional<ProductEntity> byId = productRepository.findById(id);
        if (byId.isPresent()) {
            ProductEntity productEntity = byId.get();
            productEntity.getImages().add(ProductImageEntity.builder()
                    .imageUrl(imageDTO.getImageUrl())
                    .imageType(imageDTO.getImageType())
                    .imageTitle(imageDTO.getImageTitle())
                    .imageDescription(imageDTO.getImageDescription())
                    .product(productEntity)
                    .build());
            productRepository.save(productEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductDTO> getBestSellingProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream().map(EntityDTOConverter::convertProductEntityToDTO).toList();
    }

    @Override
    public List<ProductDTO> getAllProductsByCategoryId(String category) {
        return productRepository.findByCategory(category).stream().map(EntityDTOConverter::convertProductEntityToDTO).toList();
    }

    @Override
    public boolean checkCategory(String category) {
        return productRepository.existsByCategory(category);
    }

    @Override
    public List<String> getAllCategoriesForSupplier(UUID id) {
        return productRepository.findBySupplierId(id).stream().map(ProductEntity::getCategory).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProductsForSupplier(UUID supplierId, String searchQuery) {
        List<ProductEntity> productEntityList = productRepository.findBySupplierIdAndNameContainingIgnoreCase(supplierId, searchQuery);
        List<ProductDTO> collect = productEntityList.stream().map(EntityDTOConverter::convertProductEntityToDTO).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<ProductDTO> searchProducts(String searchQuery) {
        List<ProductEntity> productEntityList = productRepository.findByNameContainingIgnoreCase(searchQuery);
        List<ProductDTO> collect = productEntityList.stream().map(EntityDTOConverter::convertProductEntityToDTO).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<ProductDTO> filterProducts(UUID supplierId, String category, Boolean isAvailable, String priceMin, String priceMax, String[] sort) {
        Specification<ProductEntity> spec = Specification.where(ProductEntitySpecification.hasSupplierId(supplierId));

        if (category != null) {
            spec = spec.and(ProductEntitySpecification.hasCategory(category));
        }

        if (isAvailable != null) {
            spec = spec.and(ProductEntitySpecification.isAvailable(isAvailable));
        }

        if (priceMin != null) {
            spec = spec.and(ProductEntitySpecification.hasPriceGreaterThan(priceMin));
        }

        if (priceMax != null) {
            spec = spec.and(ProductEntitySpecification.hasPriceLessThan(priceMax));
        }

        Sort sortOrder = Sort.by(getSortOrders(sort));
        List<ProductEntity> productEntityList = productRepository.findAll(spec, sortOrder);
        return productEntityList.stream().map(EntityDTOConverter::convertProductEntityToDTO).collect(Collectors.toList());
    }

    private List<Sort.Order> getSortOrders(String[] sort) {
        return List.of(sort).stream()
                .map(order -> {
                    String[] _order = order.split(",");
                    return new Sort.Order(Sort.Direction.fromString(_order[1]), _order[0]);
                }).toList();
    }
}
