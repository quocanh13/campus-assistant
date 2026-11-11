package com.example.backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchAdminRequest {
    @Size(max = 100, message = "{validation.admin.name.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.admin.name.required}")
    private String name;
}
