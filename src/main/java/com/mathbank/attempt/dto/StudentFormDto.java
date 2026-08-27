package com.mathbank.attempt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentFormDto {
    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    private String grade;

    private String memo;
}
