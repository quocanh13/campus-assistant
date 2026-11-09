package com.example.backend.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "jwt")
@Getter
@AllArgsConstructor
public class JwtProperties{
    private String secret;
    private long expiration;
}
