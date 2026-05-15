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
public class ProblemListDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private List<Tag> tagList;
}
