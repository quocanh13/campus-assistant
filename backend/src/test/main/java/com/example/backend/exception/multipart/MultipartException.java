package com.example.backend.exception.multipart;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class MultipartException extends Exception {
    @NonNull
    private final HttpStatusCode statusCode;
    private final String code;

    public MultipartException(MultipartError error){
        super(error.name());
        code = error.name();
        statusCode = error.getStatusCode();
    }

    @NonNull
    public HttpStatusCode getStatusCode(){
        return this.statusCode;
    }
}
