package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryDTO {
    private UUID id;
    private String categoryName;
}
