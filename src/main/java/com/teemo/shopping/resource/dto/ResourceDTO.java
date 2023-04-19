package com.teemo.shopping.resource.dto;

import com.teemo.shopping.resource.domain.Resource;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResourceDTO {
    @Builder
    protected ResourceDTO(String origin, String path, String type) {
        this.origin = origin;
        this.path = path;
        this.type = type;
    }
    public static ResourceDTO from(Resource resource) {
        return ResourceDTO.builder()
            .origin(resource.getOrigin())
            .path(resource.getPath())
            .type(resource.getType())
            .build();
    }
    private final String origin;
    private final String path;
    private final String type;
}
