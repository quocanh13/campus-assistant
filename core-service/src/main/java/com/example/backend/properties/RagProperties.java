package com.example.backend.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "services.rag")
@Getter
@AllArgsConstructor
public class RagProperties {
    @NonNull
    private final String baseUrl;
}