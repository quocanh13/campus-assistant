package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class NewMajorRequest {
    @NotBlank(message = "{validation.major.code.required}")
    private String code;

    @NotBlank(message = "{validation.major.name.required}")
    private String name;

    @JsonProperty("instruction_language")
    @NotBlank(message = "{validation.major.instruction_language.required}")
    private String instructionLanguage;

    @JsonProperty("unit_code")
    @NotBlank(message = "{validation.major.unit_code.required}")
    private String unitCode;

    @JsonProperty("program_type")
    @Size(max = 100, message = "{validation.major.program_type.length}")
    private String programType;
}
