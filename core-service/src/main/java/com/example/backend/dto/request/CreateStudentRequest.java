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
public class CreateStudentRequest {
    @JsonAlias("student_id")
    @NotNull(message = "{validation.student.id.required}")
    @Positive(message = "{validation.student.id.positive}")
    private Integer id;

    @JsonProperty("account_id")
    @JsonAlias("accountId")
    @NotNull(message = "{validation.student.account_id.required}")
    private UUID accountId;

    @NotBlank(message = "{validation.student.name.required}")
    @Size(max = 100, message = "{validation.student.name.length}")
    private String name;

    @NotNull(message = "{validation.student.cohort.required}")
    @Positive(message = "{validation.student.cohort.positive}")
    private Integer cohort;

    @NotBlank(message = "{validation.student.major.required}")
    @Size(max = 10, message = "{validation.student.major.length}")
    private String major;
}
