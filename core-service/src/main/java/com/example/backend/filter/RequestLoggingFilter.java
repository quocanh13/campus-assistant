package com.example.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = -1000)
public class RequestLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
        ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        StringBuffer url = req.getRequestURL();
        String method = req.getMethod();

        logger.info(method + " " + url);

        chain.doFilter(request, response);
    }
}