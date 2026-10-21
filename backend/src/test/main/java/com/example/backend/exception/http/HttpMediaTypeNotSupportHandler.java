package com.example.backend.exception.http;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice 
@AllArgsConstructor 
@Order (1)
public class HttpMediaTypeNotSupportHandler {
    
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(
        HttpMediaTypeNotSupportedException exception
    ){
        BaseResponse<Void> response = BaseResponse.<Void>builder()
            .code("UNSUPORTED_MEDIA_TYPE")
            .message(exception.getMessage()).build();
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
