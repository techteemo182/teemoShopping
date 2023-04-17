package com.teemo.shopping.resource.controller;

import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.resource.service.ResourceService;
import com.teemo.shopping.security.PermissionChecker;
import io.swagger.v3.oas.annotations.Operation;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Value("${file.path}")
    private String filePath;

    @Operation(operationId = "리소스 추가(파일)", summary = "리소스 추가", tags = {"리소스"})
    @PostMapping(value = "", consumes = "application/x-www-form-urlencoded")
    public String add(@RequestParam("file") MultipartFile file) throws Exception {
        permissionChecker.checkAdminAndThrow();
        //todo: file 저장한곳 연결 static-location
        UUID uuid = UUID.randomUUID();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String oldFileName = file.getOriginalFilename();
        var extensionMatcher = Pattern.compile(".*\\.(.*)$").matcher(oldFileName);
        String extension = null;
        if (extensionMatcher.find()) {
            extension = extensionMatcher.group(1);
        }
        byte[] oldFileNameBytes = digest.digest(oldFileName.getBytes(StandardCharsets.UTF_8));
        String hashedFileName = StringUtils.join(
            Arrays.stream(ArrayUtils.toObject(oldFileNameBytes))
                .map(b -> Integer.toHexString(b & 0xff)).toArray());
        String newFileName = uuid + hashedFileName;
        if (extension != null) {
            newFileName += "." + extension;
        }
        String newFilePath = filePath + newFileName;    // UUID + HASH(fileName) + .extension
        String type = file.getContentType();
        try {
            file.transferTo(new File(newFilePath));
        } catch (Exception e) {
            throw new IOException("파일 생성 실패");
        }
        resourceService.add(ResourceDTO.builder().path(newFilePath).type(type).build());
        return "success";
    }

    @Operation(operationId = "리소스 조회", summary = "리소스 조회", tags = {"리소스"})
    @GetMapping("/{resourceId}")
    public ResourceDTO get(@PathVariable Long resourceId) throws Exception {
        return resourceService.get(resourceId);
    }
}
