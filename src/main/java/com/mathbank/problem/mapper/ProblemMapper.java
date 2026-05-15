package com.mathbank.problem.mapper;

import com.mathbank.common.util.PageRequest;
import com.mathbank.problem.domain.Problem;
import com.mathbank.problem.dto.ProblemDetailDto;
import com.mathbank.problem.dto.ProblemListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProblemMapper {
    List<ProblemListDto> findAll(PageRequest pageRequest);
    int countAll();
    Problem findById(Long id);
    ProblemDetailDto findDetailById(Long id);
    void insert(Problem problem);
    void update(Problem problem);
    void deleteById(Long id);
    void insertTags(@Param("problemId") Long problemId, @Param("tagIds") List<Long> tagIds);
    void deleteTagsByProblemId(Long problemId);
}
