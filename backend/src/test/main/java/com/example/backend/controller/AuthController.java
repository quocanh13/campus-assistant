package com.example.backend.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.auth.LoginResponse;
import com.example.backend.service.AuthService;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService AuthService){
        this.authService = AuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse loginResponse = authService.login(loginRequest);
        BaseResponse<LoginResponse> response = BaseResponse.success("Login successfully", loginResponse);
        return ResponseEntity.ok().body(response);
    }
    
}
