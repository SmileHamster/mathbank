package com.mathbank.examsheet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExamSheetListDto {
    private Long id;
    private String name;
    private Integer totalCount;
    private LocalDateTime createdAt;
}
