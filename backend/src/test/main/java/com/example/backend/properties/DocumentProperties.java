package com.example.backend.properties;

import java.util.ArrayList;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "document")
public class DocumentProperties {
    private final ArrayList<String> allowedType;
}
