package com.ayiko.backend.service.impl;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.ProductDTO;
import com.ayiko.backend.repository.CustomerRepository;
import com.ayiko.backend.repository.ProductRepository;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.ProductEntity;
import com.ayiko.backend.service.ProductService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        ProductEntity save = repository.save(EntityDTOConverter.convertProductDTOToEntity(productDTO));
        return EntityDTOConverter.convertProductEntityToDTO(save);
    }

    @Override
    public ProductDTO updateProduct(UUID id, ProductDTO productDTO) {
        Optional<ProductEntity> byId = repository.findById(id);
        if (byId.isPresent()) {
            ProductEntity productEntity = EntityDTOConverter.convertProductDTOToEntity(productDTO);
            productEntity.setId(byId.get().getId());
            ProductEntity save = repository.save(productEntity);
            return EntityDTOConverter.convertProductEntityToDTO(save);
        }
        return null;
    }

    @Override
    public boolean deleteProduct(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        return repository.findById(id).map(EntityDTOConverter::convertProductEntityToDTO).orElse(null);
    }

    @Override
    public List<ProductDTO> getAllProductsForSupplier(UUID supplierId) {
        return repository.findBySupplierId(supplierId).stream().map(EntityDTOConverter::convertProductEntityToDTO).toList();
    }
}
