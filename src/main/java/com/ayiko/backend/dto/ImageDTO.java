package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ImageDTO {
    private UUID id;
    private String imageUrl;
    private String imageType;
    private String imageTitle;
    private String imageDescription;
    private boolean isProfilePicture;
}
