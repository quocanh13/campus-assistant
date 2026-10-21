package com.example.backend.mapper;

import com.example.backend.dto.JwtData;
import com.example.backend.dto.response.auth.LoginUserInfo;
import com.example.backend.dto.response.auth.RegisterResponse;
import com.example.backend.dto.response.user.CreateUserResponse;
import com.example.backend.dto.response.user.GetUserResponse;
import com.example.backend.entity.User;

public class UserMapper {
    public static RegisterResponse toRegisterResponse(User user){
        return new RegisterResponse(
            user.getId(),
            user.getUsername()
        );
    }

    public static GetUserResponse toGetUserResponse(User user){
        GetUserResponse getUserResponse = GetUserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .role(user.getRole().name())
            .build();
        return getUserResponse;
    }

    public  static CreateUserResponse toCreateUserResponse(User user) {
        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .role(user.getRole().name())
            .build();
        return createUserResponse;
    }   

    public static LoginUserInfo toLoginUserInfo(User user){
        LoginUserInfo userInfo = LoginUserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .role(user.getRole().name())
            .name(user.getName())
            .build();
        return userInfo;
    }

    public static JwtData toJwtData(User user) {
        return new JwtData(
            user.getId(), 
            user.getUsername(),
            user.getRole()
        );
    }
}
