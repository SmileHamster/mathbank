package com.mathbank.examsheet.service;

import com.mathbank.common.exception.ExamSheetException;
import com.mathbank.common.exception.ResourceNotFoundException;
import com.mathbank.examsheet.domain.ExamSheet;
import com.mathbank.examsheet.domain.ExamSheetProblem;
import com.mathbank.examsheet.dto.ExamSheetCreateDto;
import com.mathbank.examsheet.dto.ExamSheetDetailDto;
import com.mathbank.examsheet.dto.ExamSheetListDto;
import com.mathbank.examsheet.mapper.ExamSheetMapper;
import com.mathbank.problem.domain.Tag;
import com.mathbank.problem.mapper.ProblemMapper;
import com.mathbank.problem.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamSheetService {

    private final ExamSheetMapper examSheetMapper;
    private final ProblemMapper problemMapper;
    private final TagMapper tagMapper;

    @Transactional
    public Long createExamSheet(ExamSheetCreateDto dto, Long memberId) {
        int total = dto.getTotalCount();
        if (total <= 0) {
            throw new ExamSheetException("문제 수를 1개 이상 입력하세요.");
        }

        Map<Long, String> difficultyNames = tagMapper.findByType("DIFFICULTY").stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getTagValue));
        Map<Long, String> unitNames = tagMapper.findByType("UNIT").stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getTagValue));

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
                String diffName = difficultyNames.getOrDefault(difficultyTagId, "난이도 " + difficultyTagId);
                String breakdown = buildUnitBreakdown(dto, difficultyTagId, unitNames);
                throw new ExamSheetException(
                        "'" + diffName + "' 난이도 문제가 부족합니다. " +
                        "(필요 " + count + "문제, 보유 " + candidates.size() + "문제" + breakdown + ")");
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
                .gradeTagId(dto.getGradeTagId())
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

    private String buildUnitBreakdown(ExamSheetCreateDto dto, Long difficultyTagId, Map<Long, String> unitNames) {
        List<Long> unitTagIds = dto.getUnitTagIds();
        if (unitTagIds == null || unitTagIds.size() <= 1) return "";

        StringBuilder sb = new StringBuilder(" / 단원별: ");
        for (int i = 0; i < unitTagIds.size(); i++) {
            Long unitId = unitTagIds.get(i);
            int cnt = problemMapper.findIdsByCondition(
                    dto.getGradeTagId(), dto.getSemesterTagId(),
                    List.of(unitId), difficultyTagId).size();
            sb.append(unitNames.getOrDefault(unitId, "단원 " + unitId))
              .append(" ").append(cnt).append("문제");
            if (i < unitTagIds.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    @Transactional
    public void deleteExamSheet(Long id) {
        examSheetMapper.deleteProblemsById(id);
        examSheetMapper.deleteById(id);
    }
}
