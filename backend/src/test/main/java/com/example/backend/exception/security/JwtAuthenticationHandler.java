package com.example.backend.exception.security;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
@Order(1)
public class JwtAuthenticationHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<BaseResponse<Void>> handleJwtAuthentication(
        JwtAuthenticationException exception
    ){
        String message = messageSource.getMessage(
            "exception.security.jwt" + exception.getCode(), 
            null,
            LocaleContextHolder.getLocale()
        );
        BaseResponse<Void> responseData = BaseResponse.<Void>builder()
            .code(exception.getCode())
            .message(message)
            .build();
        return ResponseEntity.status(exception.getStatusCode()).body(responseData);
    }
}
