package com.mathbank.attempt.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    private Long id;
    private String name;
    private String grade;
    private String memo;
    private LocalDateTime createdAt;
}
