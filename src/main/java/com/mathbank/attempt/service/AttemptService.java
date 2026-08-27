package com.mathbank.attempt.service;

import com.mathbank.attempt.domain.StudentAnswer;
import com.mathbank.attempt.dto.AttemptProblemDto;
import com.mathbank.attempt.dto.StudentAttemptSummaryDto;
import com.mathbank.attempt.mapper.AttemptMapper;
import com.mathbank.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptMapper attemptMapper;

    public List<StudentAttemptSummaryDto> getAttemptSummaries(Long studentId) {
        return attemptMapper.findSummariesByStudent(studentId);
    }

    public List<AttemptProblemDto> getAttemptProblems(Long studentId, Long examSheetId) {
        List<AttemptProblemDto> problems = attemptMapper.findAttemptProblems(studentId, examSheetId);
        if (problems.isEmpty()) {
            throw new ResourceNotFoundException("시험지를 찾을 수 없습니다: " + examSheetId);
        }
        return problems;
    }

    @Transactional
    public void saveAttempt(Long studentId, Long examSheetId, List<Long> allProblemIds, List<Long> correctProblemIds) {
        Set<Long> correctSet = correctProblemIds == null ? new HashSet<>() : new HashSet<>(correctProblemIds);

        List<StudentAnswer> answers = new ArrayList<>();
        for (Long problemId : allProblemIds) {
            answers.add(StudentAnswer.builder()
                    .studentId(studentId)
                    .examSheetId(examSheetId)
                    .problemId(problemId)
                    .isCorrect(correctSet.contains(problemId))
                    .build());
        }

        attemptMapper.deleteAnswers(studentId, examSheetId);
        attemptMapper.insertAnswers(answers);
    }
}
