package com.mathbank.problem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemTag {
    private Long problemId;
    private Long tagId;
}
