package com.example.backend.security;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend.config.JwtConfig;
import com.example.backend.entity.AccountRole;
import com.example.backend.exception.security.JwtAuthenticationError;
import com.example.backend.exception.security.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateToken(JwtData jwtData) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .claim("account_id", jwtData.getAccountId().toString())
                .claim("role", jwtData.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public JwtData verifyToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtConfig.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String accountId = claims.get("account_id", String.class);
            String role = claims.get("role", String.class);

            if (accountId == null || role == null) {
                throw new JwtAuthenticationException(JwtAuthenticationError.INVALID_TOKEN);
            }

            return new JwtData(
                    UUID.fromString(accountId),
                    AccountRole.valueOf(role));
        } catch (ExpiredJwtException exception) {
            throw new JwtAuthenticationException(JwtAuthenticationError.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException(JwtAuthenticationError.INVALID_TOKEN);
        }
    }
}
