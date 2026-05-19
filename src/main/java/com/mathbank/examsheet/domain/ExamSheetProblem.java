package com.mathbank.examsheet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExamSheetProblem {
    private Long examSheetId;
    private Long problemId;
    private Integer sortOrder;
}
