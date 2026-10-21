package com.example.backend.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.backend.dto.request.CreateUserRequest;
import com.example.backend.dto.response.user.CreateUserResponse;
import com.example.backend.dto.response.user.GetUserResponse;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.PasswordService;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordService passwordService;

    public UserService(
        UserRepository userRepository, 
        CurrentUserService currentUserService,
        PasswordService passwordService
    ){
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.passwordService = passwordService;
    }

    public GetUserResponse getUser(UUID id){
        Optional<User> optinalUser = this.userRepository.findById(id);
        User user = optinalUser.orElseThrow(()->{
            throw new BusinessException(BusinessError.USER_NOT_FOUND);
        });
        return UserMapper.toGetUserResponse(user);
    }

    public GetUserResponse getMe() {
        Optional<User> optinalUser = this.userRepository.findById(currentUserService.getId());
        User user = optinalUser.orElseThrow(()->{
            throw new BusinessException(BusinessError.USER_NOT_FOUND);
        });
        return UserMapper.toGetUserResponse(user);
    }

    public CreateUserResponse createUser(CreateUserRequest createUserRequest){
        if(currentUserService.getRole() != Role.ADMIN){
            throw new BusinessException(BusinessError.CREATE_USER_DENIED);
        }
        User user = User.builder()
            .id(UUID.randomUUID())
            .name(createUserRequest.getName())
            .username(createUserRequest.getUsername())
            .hashedPassword(passwordService.hash(createUserRequest.getPassword()))
            .role(Role.STUDENT)
            .build();
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(BusinessError.DUPLICATE_USERNAME);
        }
        return UserMapper.toCreateUserResponse(user);
    }
}
