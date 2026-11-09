package com.example.backend.service;

import java.util.Locale;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateAccountRequest;
import com.example.backend.dto.response.account.AccountResponse;
import com.example.backend.entity.Account;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.AccountMapper;
import com.example.backend.repository.AccountRepository;
import com.example.backend.security.PasswordService;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordService passwordService;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (accountRepository.existsByEmail(email)) {
            throw new BusinessException(
                    BusinessError.ACCOUNT_EMAIL_EXISTS,
                    ResourceUtil.createArgs(email));
        }

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordService.hash(request.getPassword()))
                .role(request.getRole())
                .build();

        try {
            account = accountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(
                    BusinessError.ACCOUNT_EMAIL_EXISTS,
                    ResourceUtil.createArgs(email));
        }

        return AccountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.ACCOUNT_NOT_FOUND,
                        ResourceUtil.createArgs(id)));

        return AccountMapper.toResponse(account);
    }
}
