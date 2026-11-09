package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class NewUnitRequest {
    @NotBlank (message = "{validation.unit.code.required}")
    private String code;

    @NotBlank (message = "{validation.unit.name.required}")
    private String name;
}
