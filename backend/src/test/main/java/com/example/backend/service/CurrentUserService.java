package com.example.backend.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.JwtData;
import com.example.backend.entity.Role;

@Service
public class CurrentUserService {
    public JwtData getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtData jwtData = (JwtData)authentication.getPrincipal();
        return jwtData;
    }

    public String getUername(){
        return getUser().getUsername();
    }
    public UUID getId(){
        return getUser().getId();
    }
    public Role getRole(){
        return getUser().getRole();
    }
}
