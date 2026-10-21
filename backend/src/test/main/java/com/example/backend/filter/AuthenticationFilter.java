package com.example.backend.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.dto.JwtData;
import com.example.backend.exception.security.JwtAuthenticationError;
import com.example.backend.exception.security.JwtAuthenticationException;
import com.example.backend.security.JwtAuthenticationEntryPoint;
import com.example.backend.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtAuthenticationEntryPoint entryPoint;

    public AuthenticationFilter(
        JwtService jwtService,
        JwtAuthenticationEntryPoint entryPoint
    ){
        this.jwtService = jwtService;
        this.entryPoint = entryPoint;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = null;

            String cookieToken = getTokenFromCookie(request.getCookies());
            String headerToken = getTokenFromHeader(request.getHeader("Authorization"));

            token = headerToken == null ? cookieToken : headerToken;

            if(token == null){
                entryPoint.commence(request, response, new JwtAuthenticationException(JwtAuthenticationError.TOKEN_NOT_FOUND));
                return;
            }

            JwtData jwtData = jwtService.verifyToken(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtData, 
                null, 
                Collections.emptyList()
            );
            SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e){
            JwtAuthenticationException authenticationException;
            if(e.getClass() == JwtAuthenticationException.class){
                authenticationException = (JwtAuthenticationException) e;
            } else {
                throw new ServletException(e);
            }
            entryPoint.commence(request, response, authenticationException);
        }
    }

    private static String getTokenFromCookie(Cookie[] cookies){
        if(cookies == null) return null;
        String token = null;
        for(Cookie cookie : cookies){
            if(!cookie.getName().equals("token"))
                continue;
            token = cookie.getValue();
        }
        return token;
    }

    private static String getTokenFromHeader(String authorization){
        if(authorization == null) return null;
        String token = null;
        String[] parts = authorization.split(" ");
        if(parts.length >= 2){
            token = parts[1];
        }
        return token;
    }
}
