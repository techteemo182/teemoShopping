package com.teemo.shopping.resource.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;
import com.teemo.shopping.core.exception.ServiceException;
import com.teemo.shopping.game.domain.GamesResources;
import com.teemo.shopping.resource.domain.Resource;
import com.teemo.shopping.resource.dto.ResourceDTO;
import com.teemo.shopping.resource.repository.ResourceRepository;
import com.teemo.shopping.review.domain.Review;
import jakarta.persistence.Access;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ResourceService {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.domain}")
    private String s3domain;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private AmazonS3 s3Client;
    public Resource getResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).get();
        return resource;
    }
    public Long add(MultipartFile file) throws Exception {
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
        String newFilePath = newFileName;    // UUID + HASH(fileName) + .extension
        String type = file.getContentType();

        if(!s3Client.doesBucketExistV2(bucketName)) {
            throw ServiceException.of("S3 버킷이 존재하지 않음");
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        s3Client.putObject(bucketName, newFilePath, file.getInputStream(), objectMetadata);
        AccessControlList acl = s3Client.getObjectAcl(bucketName, newFilePath);
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3Client.setObjectAcl(bucketName, newFilePath, acl);
        String subdomain = bucketName;
        String protocol = "https:";
        String origin = protocol + "//" + subdomain + "." + s3domain;
        Resource resource = Resource
            .builder()
            .origin(origin)
            .path(newFilePath)
            .type(type)
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
