package com.teemo.shopping.resource.dto;

import com.teemo.shopping.resource.domain.Resource;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResourceDTO {
    @Builder
    protected ResourceDTO(String path, String type) {
        this.path = path;
        this.type = type;
    }
    public static ResourceDTO from(Resource resource) {
        return ResourceDTO.builder()
            .path(resource.getPath())
            .type(resource.getType())
            .build();
    }

    private final String path;
    private final String type;
}
