package com.example.backend.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Configuration;

import com.example.backend.properties.JwtProperties;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Configuration
@Getter
public class JwtConfig {
    private final SecretKey secretKey;
    private final long expiration;

    public JwtConfig(JwtProperties jwtProperties){
        this.expiration = jwtProperties.getExpiration();
        this.secretKey = Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }
}
