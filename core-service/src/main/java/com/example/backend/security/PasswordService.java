package com.example.backend.security;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public @NonNull String hash(@NonNull String password){
        String hashedPassword = encoder.encode(password);
        if(hashedPassword == null)
            throw new IllegalStateException("hashedPassword is null");
        return hashedPassword;
    }

    public boolean verify(String password, String hashedPassword){
        return encoder.matches(password, hashedPassword);
    }
}
