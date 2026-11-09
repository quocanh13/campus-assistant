package com.example.backend.security;

import org.springframework.stereotype.Service;

import com.example.backend.config.JwtConfig;
import com.example.backend.exception.security.JwtAuthenticationError;
import com.example.backend.exception.security.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig; 

    // public String generateToken(JwtData jwtData){
    //     String token = Jwts.builder()
    //                     .claim("id", jwtData.getId().toString())
    //                     .claim("username", jwtData.getUsername())
    //                     .claim("role", jwtData.getRole().name())
    //                     .issuedAt(new Date())
    //                     .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
    //                     .signWith(jwtConfig.getSecretKey())
    //                     .compact();
    //     return token;
    // }

    // public JwtData verifyToken(String token){
    //     try{
    //         Claims claims = Jwts.parser()
    //                             .verifyWith(jwtConfig.getSecretKey())
    //                             .build()
    //                             .parseSignedClaims(token)
    //                             .getPayload();
    //         UUID id = UUID.fromString(claims.get("id", String.class));
    //         String username = claims.get("username", String.class);
    //         Role role = Role.valueOf(claims.get("role", String.class));
    //         if(id == null || username == null)
    //             throw new JwtAuthenticationException(JwtAuthenticationError.TOKEN_NOT_FOUND);
            
    //         return new JwtData(id, username, role);
            
    //     }catch(ExpiredJwtException exception){
    //         throw new JwtAuthenticationException(JwtAuthenticationError.TOKEN_EXPIRED);
    //     } catch (JwtException exception){
    //         throw new JwtAuthenticationException(JwtAuthenticationError.INVALID_TOKEN);
    //     }
    // }
}

