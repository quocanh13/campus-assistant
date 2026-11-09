package com.example.backend.exception.business;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor 
public class BusinessException extends RuntimeException{
    private final String code;
    @NonNull
    private final HttpStatusCode statusCode;
    private final Object[] args;
    
    public BusinessException(BusinessError error, Object[] args){
        this.statusCode = error.getStatusCode();
        this.code = error.name();
        this.args = args;
    }

    public BusinessException(BusinessError error){
        this.statusCode = error.getStatusCode();
        this.code = error.name();
        this.args = null;
    }

    @NonNull
    public HttpStatusCode getStatusCode(){
        return this.statusCode;
    }
}

