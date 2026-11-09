package com.example.backend.exception.storage;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {
    private final String code;
    @NonNull
    private final HttpStatusCode statusCode;

    public StorageException(StorageError error){
        super(error.name());
        code = error.name();
        statusCode = error.getStatusCode();
    }

    @NonNull
    public HttpStatusCode getStatusCode(){
        return this.statusCode;
    }
}
