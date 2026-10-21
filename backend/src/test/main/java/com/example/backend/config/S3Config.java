package com.example.backend.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend.properties.S3Properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Getter
@AllArgsConstructor
public class S3Config {
    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey());
        S3Client client = S3Client.builder()
            .endpointOverride(URI.create(s3Properties.getEndpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.AP_SOUTHEAST_7)
            .forcePathStyle(true)
            .build();
        return client;
    }
}