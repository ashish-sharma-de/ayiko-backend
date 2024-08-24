package com.ayiko.backend.service.core;

import com.ayiko.backend.dto.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDTO addCategory(CategoryDTO dto);
    CategoryDTO getCategory(UUID categoryId);
    List<CategoryDTO> getAllCategories();
    boolean deleteCategory(UUID categoryId);
    CategoryDTO updateCategory(CategoryDTO dto);
}
