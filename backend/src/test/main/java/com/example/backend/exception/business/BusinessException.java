package com.example.backend.exception.business;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final String code;
    @NonNull
    private final HttpStatusCode statusCode;
    
    public BusinessException(BusinessError error){
        this.statusCode = error.getStatusCode();
        this.code = error.name();
    }

    @NonNull
    public HttpStatusCode getStatusCode(){
        return this.statusCode;
    }
}

