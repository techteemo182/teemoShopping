package com.teemo.shopping.resource.controller;

import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.resource.service.ResourceService;
import com.teemo.shopping.security.PermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PermissionChecker permissionChecker;
    @PostMapping("")
    public String add(@RequestBody ResourceDTO resourceDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        resourceService.add(resourceDTO);
        return "success";
    }
    @GetMapping("/{resourceId}")
    public ResourceDTO get(@PathVariable Long resourceId) throws Exception {
        return resourceService.get(resourceId);
    }
}
