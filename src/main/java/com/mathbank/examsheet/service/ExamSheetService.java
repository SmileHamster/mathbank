package com.mathbank.examsheet.service;

import com.mathbank.common.exception.ExamSheetException;
import com.mathbank.common.exception.ResourceNotFoundException;
import com.mathbank.examsheet.domain.ExamSheet;
import com.mathbank.examsheet.domain.ExamSheetProblem;
import com.mathbank.examsheet.dto.ExamSheetCreateDto;
import com.mathbank.examsheet.dto.ExamSheetDetailDto;
import com.mathbank.examsheet.dto.ExamSheetListDto;
import com.mathbank.examsheet.mapper.ExamSheetMapper;
import com.mathbank.problem.mapper.ProblemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamSheetService {

    private final ExamSheetMapper examSheetMapper;
    private final ProblemMapper problemMapper;

    @Transactional
    public Long createExamSheet(ExamSheetCreateDto dto, Long memberId) {
        int total = dto.getTotalCount();
        if (total <= 0) {
            throw new ExamSheetException("문제 수를 1개 이상 입력하세요.");
        }

        List<ExamSheetProblem> selected = new ArrayList<>();
        int sortOrder = 1;

        for (Map.Entry<Long, Integer> entry : dto.getDifficultyDistribution().entrySet()) {
            Long difficultyTagId = entry.getKey();
            int count = entry.getValue() != null ? entry.getValue() : 0;
            if (count <= 0) continue;

            List<Long> candidates = problemMapper.findIdsByCondition(
                    dto.getGradeTagId(),
                    dto.getSemesterTagId(),
                    dto.getUnitTagIds(),
                    difficultyTagId
            );

            if (candidates.size() < count) {
                throw new ExamSheetException(
                        "선택 조건에 맞는 문제가 부족합니다. (난이도 태그 ID=" + difficultyTagId +
                        ", 필요=" + count + ", 보유=" + candidates.size() + ")");
            }

            Collections.shuffle(candidates);
            for (int i = 0; i < count; i++) {
                selected.add(new ExamSheetProblem(null, candidates.get(i), sortOrder++));
            }
        }

        Collections.shuffle(selected);
        for (int i = 0; i < selected.size(); i++) {
            selected.set(i, new ExamSheetProblem(null, selected.get(i).getProblemId(), i + 1));
        }

        ExamSheet examSheet = ExamSheet.builder()
                .name(dto.getName())
                .totalCount(selected.size())
                .createdBy(memberId)
                .build();
        examSheetMapper.insertExamSheet(examSheet);
        examSheetMapper.insertExamSheetProblems(examSheet.getId(), selected);

        return examSheet.getId();
    }

    public List<ExamSheetListDto> getExamSheetList(Long memberId) {
        return examSheetMapper.findAll(memberId);
    }

    public ExamSheetDetailDto getExamSheetDetail(Long id) {
        ExamSheetDetailDto detail = examSheetMapper.findDetailById(id);
        if (detail == null) {
            throw new ResourceNotFoundException("시험지를 찾을 수 없습니다: " + id);
        }
        return detail;
    }

    @Transactional
    public void deleteExamSheet(Long id) {
        examSheetMapper.deleteProblemsById(id);
        examSheetMapper.deleteById(id);
    }
}
