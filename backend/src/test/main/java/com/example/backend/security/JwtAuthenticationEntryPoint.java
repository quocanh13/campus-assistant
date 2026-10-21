package com.example.backend.security;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.backend.dto.response.BaseResponse;
import com.example.backend.exception.security.JwtAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(MessageSource messageSource, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        JwtAuthenticationException jwtAuthException = (JwtAuthenticationException) authException;
        BaseResponse<Void> body;
            body = BaseResponse.<Void>builder()
            .code(jwtAuthException.getCode())
            .message(messageSource.getMessage("exception.security.jwt." + jwtAuthException.getCode(), null, null))
            .data(null)
            .build();

        response.setStatus(jwtAuthException.getStatusCode().value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
