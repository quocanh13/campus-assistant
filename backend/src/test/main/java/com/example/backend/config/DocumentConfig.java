package com.example.backend.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Configuration;

import com.example.backend.properties.DocumentProperties;

import lombok.Getter;

@Configuration
@Getter
public class DocumentConfig {
    private final ArrayList<String> allowedType;

    public DocumentConfig(DocumentProperties documentProperties){
        allowedType = documentProperties.getAllowedType();
    }
}
