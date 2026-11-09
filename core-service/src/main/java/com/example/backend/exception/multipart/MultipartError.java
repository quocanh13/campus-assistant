package com.example.backend.exception.multipart;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public enum MultipartError {
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_REQUIRED(HttpStatus.BAD_REQUEST);

    @NonNull
    private final HttpStatusCode statusCode;
    private MultipartError(@NonNull HttpStatusCode statusCode){
        this.statusCode = statusCode;
    }
}
