package com.example.backend.exception.security;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;

import lombok.Getter;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    @NonNull
    private final HttpStatusCode statusCode;
    private final String code;

    public JwtAuthenticationException(JwtAuthenticationError error){
        super(error.name());
        this.code = error.name();
        this.statusCode = error.getStatusCode();
    }

    @NonNull
    public HttpStatusCode getStatusCode(){
        return this.statusCode;
    }
}
