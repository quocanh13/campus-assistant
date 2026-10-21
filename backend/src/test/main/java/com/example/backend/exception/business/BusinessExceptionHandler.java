package com.example.backend.exception.business;

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
public class BusinessExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(
        BusinessException exception
    ){
        BaseResponse<Void> response = BaseResponse.<Void>builder()
            .code(exception.getCode())
            .message(
                messageSource.getMessage(
                    "exception.business." + exception.getCode(), 
                    null, 
                    LocaleContextHolder.getLocale()
                )
            ).build();
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
