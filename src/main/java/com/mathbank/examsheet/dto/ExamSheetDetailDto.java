package com.mathbank.examsheet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExamSheetDetailDto {
    private Long id;
    private String name;
    private Integer totalCount;
    private LocalDateTime createdAt;
    private List<ExamSheetProblemDto> problems;
}
