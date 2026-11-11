package com.example.backend.exception.parameter;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public enum ParameterError {
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST);

    @NonNull
    private final HttpStatusCode statusCode;

    private ParameterError(@NonNull HttpStatusCode statusCode){
        this.statusCode = statusCode;
    }
}
