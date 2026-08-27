package com.mathbank.attempt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttemptProblemDto {
    private Long problemId;
    private Integer sortOrder;
    private String title;
    private String content;
    private Boolean isCorrect;
}
