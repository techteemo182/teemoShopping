package com.teemo.shopping.resource.service;

import com.teemo.shopping.resource.domain.Resource;
import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.resource.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    public Long add(ResourceDTO resourceDTO) {
        Resource resource = Resource
            .builder()
            .path(resourceDTO.getPath())
            .type(resourceDTO.getType())
            .build();
        resource = resourceRepository.save(resource);
        return resource.getId();
    }
    public void remove(Long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
    public ResourceDTO get(Long resourceId) {
        return ResourceDTO.from(resourceRepository.findById(resourceId).get());
    }
}
