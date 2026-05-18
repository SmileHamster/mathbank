package com.mathbank.problem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemSearchDto {
    private String keyword;
    private List<Long> tagIds;
    private int page = 1;
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }

    public boolean isHasCondition() {
        return (keyword != null && !keyword.isBlank())
                || (tagIds != null && !tagIds.isEmpty());
    }
}
