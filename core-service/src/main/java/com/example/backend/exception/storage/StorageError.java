package com.example.backend.exception.storage;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageError {
    UPLOAD_FILE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    GET_FILE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_FILE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR);

    
    @NonNull
    private final HttpStatusCode statusCode;
}
