package com.example.backend.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public enum BusinessError{
    DUPLICATE_USERNAME(HttpStatus.CONFLICT),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST),
    CREATE_USER_DENIED(HttpStatus.FORBIDDEN),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST),

    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND),
    DOCUMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN),
    INVALID_DOCUMENT_TYPE(HttpStatus.BAD_REQUEST),
    UPLOAD_DOCUMENT_DENIED(HttpStatus.FORBIDDEN),
    DELETE_DOCUMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    
    UNIT_NOT_FOUND(HttpStatus.NOT_FOUND),

    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND),
    MAJOR_EXISTS(HttpStatus.CONFLICT);

    @NonNull
    private final HttpStatusCode statusCode;
    private BusinessError(@NonNull HttpStatusCode statusCode){
        this.statusCode = statusCode;
    }
}
