package com.mathbank.examsheet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ExamSheetCreateDto {

    @NotBlank(message = "시험지 이름을 입력하세요.")
    private String name;

    @NotNull(message = "학년을 선택하세요.")
    private Long gradeTagId;

    private Long semesterTagId;

    private List<Long> unitTagIds;

    private Map<Long, Integer> difficultyDistribution = new LinkedHashMap<>();

    public int getTotalCount() {
        if (difficultyDistribution == null) return 0;
        return difficultyDistribution.values().stream()
                .filter(v -> v != null && v > 0)
                .mapToInt(Integer::intValue).sum();
    }
}
