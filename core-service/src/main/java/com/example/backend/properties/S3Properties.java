package com.example.backend.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "services.s3")
@Getter
@AllArgsConstructor
public class S3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String region;
}
