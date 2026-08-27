package com.mathbank.attempt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StudentAttemptSummaryDto {
    private Long examSheetId;
    private String examSheetName;
    private Integer totalCount;
    private Integer correctCount;
    private LocalDateTime submittedAt;
}
