package com.mathbank.problem.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {
    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private String answer;
    private String explanation;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
