package com.example.backend.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.auth.LoginResponse;
import com.example.backend.dto.response.auth.LoginUserInfo;
import com.example.backend.entity.Account;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.repository.AccountRepository;
import com.example.backend.security.JwtData;
import com.example.backend.security.JwtService;
import com.example.backend.security.PasswordService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessError.INVALID_CREDENTIALS));

        if (!passwordService.verify(request.getPassword(), account.getPassword())) {
            throw new BusinessException(BusinessError.INVALID_CREDENTIALS);
        }

        JwtData jwtData = new JwtData(account.getId(), account.getRole());
        String token = jwtService.generateToken(jwtData);

        LoginUserInfo userInfo = LoginUserInfo.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .build();

        return new LoginResponse(token, userInfo);
    }
}
