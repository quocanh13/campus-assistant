package com.example.backend.exception.multipart;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.example.backend.dto.response.BaseResponse;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
@Order(1)
public class MultipartExceptionHandler {
    private final MessageSource messageSource;
    private final static String PREFIX = "exception.multipart.";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse<Void>> handleSizeExceeded(MaxUploadSizeExceededException exception){
        MultipartError error = MultipartError.FILE_TOO_LARGE;
        String code = error.name();
        String message = messageSource.getMessage(PREFIX + code, null, LocaleContextHolder.getLocale());

        BaseResponse<Void> responseData = BaseResponse.<Void>builder().code(code).message(message).build();
        return ResponseEntity.status(error.getStatusCode()).body(responseData);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingPart(MissingServletRequestPartException exception){
        MultipartError error = MultipartError.FILE_REQUIRED;
        String code = error.name();
        String message = messageSource.getMessage(PREFIX + code, null, LocaleContextHolder.getLocale());

        BaseResponse<Void> responseData = BaseResponse.<Void>builder().code(code).message(message).build();
        return ResponseEntity.status(error.getStatusCode()).body(responseData);
    }
}
