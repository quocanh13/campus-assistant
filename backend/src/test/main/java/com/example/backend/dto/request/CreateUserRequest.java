package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "{validation.username.required}")
    @Size(min = 1, max = 50, message = "{validation.username.length}")
    private String username;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 1, message = "{validation.password.length}")
    private String password;

    @NotBlank(message = "{validation.name.required}")
    @Size(min = 1, max = 50, message = "{validation.name.length}")
    private String name;
}