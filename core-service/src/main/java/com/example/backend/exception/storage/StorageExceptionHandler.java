package com.example.backend.exception.storage;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@Order(1)
@AllArgsConstructor
public class StorageExceptionHandler {
    private final MessageSource messageSource;
    private static final String PREFIX = "exception.storage.";

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<BaseResponse<Void>> handleStorageException(StorageException exception){
        String message = messageSource.getMessage(
            PREFIX + exception.getCode(),
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
