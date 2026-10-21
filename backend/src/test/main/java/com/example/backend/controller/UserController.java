package com.example.backend.controller;

import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.backend.dto.request.CreateUserRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.user.CreateUserResponse;
import com.example.backend.dto.response.user.GetUserResponse;
import com.example.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final MessageSource messageSource;

    public UserController(
        UserService userService,
        MessageSource messageSource
    ){
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<GetUserResponse>> getMe() {
        GetUserResponse getUserResponse = userService.getMe();
        String message = messageSource.getMessage(
            "success.user.get",
            null, 
            LocaleContextHolder.getLocale()
        );
        BaseResponse<GetUserResponse> response = BaseResponse.success(message, getUserResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<GetUserResponse>> getUser(
        @PathVariable @NonNull UUID id
    ){
        GetUserResponse getUserResponse = userService.getUser(id);
        String message = messageSource.getMessage(
            "success.users.get",
            null, 
            LocaleContextHolder.getLocale()
        );
        BaseResponse<GetUserResponse> response = BaseResponse.success(message, getUserResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<BaseResponse<CreateUserResponse>> createUser(
        @RequestBody @Valid CreateUserRequest createUserRequest
    ) {
        CreateUserResponse createUserResponse = userService.createUser(createUserRequest);
        String message = messageSource.getMessage("success.users.create", null, LocaleContextHolder.getLocale());
        BaseResponse<CreateUserResponse> response = BaseResponse.success(message, createUserResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
