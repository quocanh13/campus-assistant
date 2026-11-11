package com.example.backend.exception.parameter;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
@Order(1)
public class ParameterExceptionHandler {
    private final MessageSource messageSource;
    private static final String PREFIX = "exception.parameter.";

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingParameter(
        MissingServletRequestParameterException exception
    ){
        ParameterError error = ParameterError.MISSING_PARAMETER;
        String code = error.name();
        String parameterName = exception.getParameterName();
        String message = messageSource.getMessage(
            PREFIX + code,
            new Object[]{parameterName},
            LocaleContextHolder.getLocale()
        );

        BaseResponse<Void> responseData = BaseResponse.<Void>builder()
            .code(code)
            .message(message)
            .detail(List.of(new BaseResponse.FieldError(
                parameterName,
                "Required parameter is missing"
            )))
            .build();

        return ResponseEntity.status(error.getStatusCode()).body(responseData);
    }
}
