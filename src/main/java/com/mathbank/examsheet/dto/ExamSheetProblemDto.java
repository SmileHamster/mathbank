package com.mathbank.examsheet.dto;

import com.mathbank.problem.domain.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExamSheetProblemDto {
    private Integer sortOrder;
    private Long problemId;
    private String title;
    private String content;
    private String answer;
    private String explanation;
    private List<Tag> tagList;
}
