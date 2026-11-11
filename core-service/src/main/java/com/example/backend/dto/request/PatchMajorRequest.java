package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchMajorRequest {
    @Pattern(regexp = ".*\\S.*", message = "{validation.major.name.required}")
    @Size(max = 100, message = "{validation.major.name.length}")
    private String name;

    @JsonProperty("instruction_language")
    @Pattern(regexp = ".*\\S.*", message = "{validation.major.instruction_language.required}")
    @Size(max = 50, message = "{validation.major.instruction_language.length}")
    private String instructionLanguage;

    @JsonProperty("unit_code")
    @Pattern(regexp = ".*\\S.*", message = "{validation.major.unit_code.required}")
    @Size(max = 10, message = "{validation.major.unit_code.length}")
    private String unitCode;

    @JsonProperty("program_type")
    @Pattern(regexp = ".*\\S.*", message = "{validation.major.program_type.required}")
    @Size(max = 100, message = "{validation.major.program_type.length}")
    private String programType;
}
