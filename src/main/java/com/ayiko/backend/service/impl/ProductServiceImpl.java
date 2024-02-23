package com.ayiko.backend.service.impl;

import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.repository.ProductRepository;
import com.ayiko.backend.repository.SupplierRepository;
import com.ayiko.backend.repository.entity.ProductEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import com.ayiko.backend.service.ProductService;
import com.ayiko.backend.service.SupplierService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            SupplierEntity supplierEntity = supplierRepository.findById(productDTO.getSupplierId()).orElseThrow(() -> new RuntimeException("Supplier not found"));
            ProductEntity productEntity = EntityDTOConverter.convertProductDTOToEntity(productDTO);
            productEntity.setSupplier(supplierEntity);
            productEntity.setId(byId.get().getId());
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
}
