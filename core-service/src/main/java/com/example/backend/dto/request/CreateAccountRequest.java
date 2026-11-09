package com.example.backend.dto.request;

import com.example.backend.entity.AccountRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequest {
    @NotBlank(message = "{validation.account.email.required}")
    @Email(message = "{validation.account.email.invalid}")
    @Size(max = 255, message = "{validation.account.email.length}")
    private String email;

    @NotBlank(message = "{validation.account.password.required}")
    @Size(min = 8, max = 72, message = "{validation.account.password.length}")
    private String password;

    @NotNull(message = "{validation.account.role.required}")
    private AccountRole role;
}
