package com.example.backend.exception;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e
    ) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                                        .code("METHOD_NOT_ALLOWED")
                                        .message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Void>> hanldeNoResourceFound(
        NoResourceFoundException exception
    ) {
        BaseResponse<Void> body = BaseResponse.<Void>builder()
        .code("RESOURCE_NOT_FOUND")
        .message(exception.getMessage())
        .data(null).build();
        return ResponseEntity.badRequest().body(body);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        List<BaseResponse.FieldError> fieldErrors = new ArrayList<BaseResponse.FieldError>();
        for (FieldError fieldError: e.getFieldErrors()){
            fieldErrors.add(new BaseResponse.FieldError(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        BaseResponse<Void> responseBody = BaseResponse.<Void>builder()
                                            .code("INVALID_DATA")
                                            .message("Invalid data")
                                            .detail(fieldErrors).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e
    ) {
        BaseResponse<Void> responseData = BaseResponse.<Void>builder()
                .code("INVALID_PARAMETER")
                .message("Invalid parameter: " + e.getName())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseData);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadable(
        HttpMessageNotReadableException exception
    ) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
            .code("BODY_NOT_READABLE")
            .message("Body is not readable")
            .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(
            Exception exception
    ) {
        logger.warn(exception.getClass().getName());
        BaseResponse<Void> response = BaseResponse.serverError();
        logger.warn(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
