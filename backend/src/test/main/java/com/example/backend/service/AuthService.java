package com.example.backend.service;

import java.util.Optional;
// import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend.dto.JwtData;
import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.response.auth.LoginResponse;
import com.example.backend.dto.response.auth.LoginUserInfo;
import com.example.backend.entity.User;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import com.example.backend.security.PasswordService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public AuthService(
        UserRepository userRepository,
        JwtService jwtService,
        PasswordService passwordService
    ){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        // System.out.println("-----------" + UUID.randomUUID());
        // System.out.println("-----------" + passwordService.hash("admin"));
    }

    public LoginResponse login(LoginRequest loginRequest){
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        User user = optionalUser.orElseThrow(()->{
            throw new BusinessException(BusinessError.USER_NOT_FOUND);
        });

        if(!passwordService.verify(loginRequest.getPassword(), user.getHashedPassword())){
            throw new BusinessException(BusinessError.INVALID_PASSWORD);
        }

        JwtData jwtData = UserMapper.toJwtData(user);
        String token = jwtService.generateToken(jwtData);
        LoginUserInfo userInfo = UserMapper.toLoginUserInfo(user);
        return new LoginResponse(token, userInfo);
    }
}
