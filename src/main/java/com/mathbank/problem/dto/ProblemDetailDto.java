package com.mathbank.problem.dto;

import com.mathbank.problem.domain.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemDetailDto {
    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private String answer;
    private String explanation;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Tag> tagList;
}
