package com.mathbank.examsheet.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSheet {
    private Long id;
    private String name;
    private Integer totalCount;
    private Long createdBy;
    private LocalDateTime createdAt;
}
