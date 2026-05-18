package com.mathbank.problem.service;

import com.mathbank.common.exception.ResourceNotFoundException;
import com.mathbank.common.util.PageInfo;
import com.mathbank.problem.domain.Problem;
import com.mathbank.problem.dto.ProblemDetailDto;
import com.mathbank.problem.dto.ProblemFormDto;
import com.mathbank.problem.dto.ProblemListDto;
import com.mathbank.problem.dto.ProblemSearchDto;
import com.mathbank.problem.mapper.ProblemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemMapper problemMapper;

    public Map<String, Object> getProblemList(ProblemSearchDto searchDto) {
        List<ProblemListDto> problems = problemMapper.search(searchDto);
        int totalCount = problemMapper.countSearch(searchDto);
        PageInfo pageInfo = new PageInfo(searchDto.getPage(), searchDto.getSize(), totalCount);

        Map<String, Object> result = new HashMap<>();
        result.put("problems", problems);
        result.put("pageInfo", pageInfo);
        result.put("searchDto", searchDto);
        return result;
    }

    public ProblemDetailDto getProblemDetail(Long id) {
        ProblemDetailDto detail = problemMapper.findDetailById(id);
        if (detail == null) {
            throw new ResourceNotFoundException("문제를 찾을 수 없습니다: " + id);
        }
        return detail;
    }

    @Transactional
    public void createProblem(ProblemFormDto dto, Long memberId) {
        Problem problem = Problem.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .answer(dto.getAnswer())
                .explanation(dto.getExplanation())
                .createdBy(memberId)
                .build();
        problemMapper.insert(problem);
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            problemMapper.insertTags(problem.getId(), dto.getTagIds());
        }
    }

    @Transactional
    public void updateProblem(Long id, ProblemFormDto dto) {
        Problem problem = problemMapper.findById(id);
        if (problem == null) {
            throw new ResourceNotFoundException("문제를 찾을 수 없습니다: " + id);
        }
        problem.setTitle(dto.getTitle());
        problem.setContent(dto.getContent());
        problem.setAnswer(dto.getAnswer());
        problem.setExplanation(dto.getExplanation());
        problemMapper.update(problem);

        problemMapper.deleteTagsByProblemId(id);
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            problemMapper.insertTags(id, dto.getTagIds());
        }
    }

    @Transactional
    public void deleteProblem(Long id) {
        problemMapper.deleteById(id);
    }
}
