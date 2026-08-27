package com.mathbank.attempt.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {
    private Long id;
    private Long studentId;
    private Long examSheetId;
    private Long problemId;
    private Boolean isCorrect;
}
