package com.ayiko.backend.repository.core;

import com.ayiko.backend.repository.core.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ProductEntitySpecification {

    public static Specification<ProductEntity> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<ProductEntity> isAvailable(boolean isAvailable) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isAvailable"), isAvailable);
    }

    public static Specification<ProductEntity> hasPriceLessThan(String price) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("unitPrice"), price);
    }

    public static Specification<ProductEntity> hasPriceGreaterThan(String price) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("unitPrice"), price);
    }

    public static Specification<ProductEntity> hasSupplierId(UUID supplierId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("supplier"), supplierId);
    }
}
