package com.ayiko.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDTO {
    private String imageUrl;
    private String imageType;
    private String imageTitle;
    private String imageDescription;
    private boolean isProfilePicture;
}
