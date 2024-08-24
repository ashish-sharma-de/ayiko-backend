package com.ayiko.backend.controller.core;

import com.ayiko.backend.dto.CategoryDTO;
import com.ayiko.backend.dto.LocationDTO;
import com.ayiko.backend.dto.order.OrderDTO;
import com.ayiko.backend.dto.order.OrderTrackingDTO;
import com.ayiko.backend.exception.ExceptionHandler;
import com.ayiko.backend.repository.core.CategoryRepository;
import com.ayiko.backend.service.core.CategoryService;
import com.ayiko.backend.service.core.OrderService;
import com.ayiko.backend.service.core.OrderTrackingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;


    @PostMapping("/add")
    public ResponseEntity addCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO category = categoryService.getCategory(categoryDTO.getId());
            if(category != null) {
                categoryService.updateCategory(categoryDTO);
                return ResponseEntity.ok().build();
            }
            categoryService.addCategory(categoryDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO category = categoryService.getCategory(categoryDTO.getId());
            if(category != null) {
                categoryService.updateCategory(categoryDTO);
                return ResponseEntity.ok().build();
            }
            categoryService.addCategory(categoryDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/get/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable UUID categoryId) {
        try {
            CategoryDTO categoryDTO = categoryService.getCategory(categoryId);
            if(categoryDTO == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            return ResponseEntity.ok(categoryDTO);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            if(categories == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/delete/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable UUID categoryId) {
        try {
            CategoryDTO categoryDTO = categoryService.getCategory(categoryId);
            if(categoryDTO == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ExceptionHandler.ERROR_INVALID_ID)).build();
            }
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

}
