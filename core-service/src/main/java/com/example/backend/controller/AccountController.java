package com.example.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateAccountRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.account.AccountResponse;
import com.example.backend.security.JwtData;
import com.example.backend.service.AccountService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<BaseResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse account = accountService.createAccount(request);
        BaseResponse<AccountResponse> response = BaseResponse.success(
                "Create account successfully", account);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<AccountResponse>>> getAccounts(
            @AuthenticationPrincipal JwtData jwtData,
            @RequestParam(defaultValue = "0") int index,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String role) {
        List<AccountResponse> accounts = accountService.getAccounts(jwtData.getAccountId(), index, limit, role);
        BaseResponse<List<AccountResponse>> response = BaseResponse.success(
                "Get accounts successfully", accounts);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AccountResponse>> getAccount(
            @PathVariable UUID id) {
        AccountResponse account = accountService.getAccount(id);
        BaseResponse<AccountResponse> response = BaseResponse.success(
                "Get account successfully", account);
        return ResponseEntity.ok().body(response);
    }
}
