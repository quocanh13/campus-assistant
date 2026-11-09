package com.example.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "{validation.account.email.required}")
    @Email(message = "{validation.account.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.account.password.required}")
    private String password;
}
