package com.example.backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchStudentRequest {
    @Size(max = 100, message = "{validation.student.name.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.student.name.required}")
    private String name;

    @Positive(message = "{validation.student.cohort.positive}")
    private Integer cohort;

    @Size(max = 10, message = "{validation.student.major.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.student.major.required}")
    private String major;
}
