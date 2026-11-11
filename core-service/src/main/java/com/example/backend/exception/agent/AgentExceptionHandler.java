package com.example.backend.exception.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.response.BaseResponse;

@RestControllerAdvice
public class AgentExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AgentException.class)
    public ResponseEntity<BaseResponse<Object>> handleAgentException(AgentException ex) {
        logger.error("Agent service error: code={}, message={}", ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(BaseResponse.success(ex.getMessage(), null));
    }
}
