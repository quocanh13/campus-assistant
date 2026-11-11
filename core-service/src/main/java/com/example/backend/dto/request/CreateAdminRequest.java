package com.example.backend.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAdminRequest {
    @JsonAlias("admin_id")
    @NotNull(message = "{validation.admin.id.required}")
    @Positive(message = "{validation.admin.id.positive}")
    private Integer id;

    @JsonProperty("account_id")
    @JsonAlias("accountId")
    @NotNull(message = "{validation.admin.account_id.required}")
    private UUID accountId;

    @NotBlank(message = "{validation.admin.name.required}")
    @Size(max = 100, message = "{validation.admin.name.length}")
    private String name;
}
