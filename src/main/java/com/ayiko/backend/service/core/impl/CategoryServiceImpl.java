package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.dto.CategoryDTO;
import com.ayiko.backend.repository.core.*;
import com.ayiko.backend.repository.core.entity.*;
import com.ayiko.backend.service.core.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public CategoryDTO addCategory(CategoryDTO dto) {
        CategoryEntity entity = CategoryEntity.builder().categoryName(dto.getCategoryName()).build();
        CategoryEntity save = categoryRepository.save(entity);
        dto.setId(save.getId());
        return dto;
    }

    @Override
    public CategoryDTO getCategory(UUID categoryId) {
        CategoryEntity entity = categoryRepository.findById(categoryId).get();
        return CategoryDTO.builder().id(categoryId).categoryName(entity.getCategoryName()).build();
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        return categoryEntities.stream().map(entity -> {
            return CategoryDTO.builder().categoryName(entity.getCategoryName()).id(entity.getId()).build();
        }).collect(Collectors.toList());
    }

    @Override
    public boolean deleteCategory(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
        return true;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO dto) {
        CategoryEntity categoryEntity = categoryRepository.findById(dto.getId()).get();
        categoryEntity.setCategoryName(dto.getCategoryName());
        categoryRepository.save(categoryEntity);
        return dto;
    }
}
