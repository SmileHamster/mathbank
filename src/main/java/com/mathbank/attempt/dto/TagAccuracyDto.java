package com.mathbank.attempt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TagAccuracyDto {
    private String label;
    private Integer totalCount;
    private Integer correctCount;
}
