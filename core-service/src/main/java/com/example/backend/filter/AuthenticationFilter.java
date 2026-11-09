package com.example.backend.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.exception.security.JwtAuthenticationError;
import com.example.backend.exception.security.JwtAuthenticationException;
import com.example.backend.security.JwtAuthenticationEntryPoint;
import com.example.backend.security.JwtData;
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
        return request.getServletPath().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = getTokenFromHeader(request.getHeader("Authorization"));
            if (token == null) {
                token = getTokenFromCookie(request.getCookies());
            }

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtData jwtData = jwtService.verifyToken(token);
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + jwtData.getRole().name()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(jwtData, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException exception) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, exception);
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
        String[] parts = authorization.split(" ");
        if(parts.length >= 2 && "Bearer".equalsIgnoreCase(parts[0])){
            return parts[1];
        }
        return null;
    }
}
