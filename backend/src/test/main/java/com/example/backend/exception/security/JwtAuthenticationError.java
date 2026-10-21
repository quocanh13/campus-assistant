package com.example.backend.exception.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public enum JwtAuthenticationError{
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED);


    @NonNull
    private final HttpStatusCode statusCode;
    private JwtAuthenticationError(@NonNull HttpStatusCode statusCode){
        this.statusCode = statusCode;
    }
}
