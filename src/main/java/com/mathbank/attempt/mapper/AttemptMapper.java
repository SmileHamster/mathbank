package com.mathbank.attempt.mapper;

import com.mathbank.attempt.domain.StudentAnswer;
import com.mathbank.attempt.dto.AttemptProblemDto;
import com.mathbank.attempt.dto.StudentAttemptSummaryDto;
import com.mathbank.attempt.dto.TagAccuracyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttemptMapper {
    List<StudentAttemptSummaryDto> findSummariesByStudent(@Param("studentId") Long studentId);
    List<AttemptProblemDto> findAttemptProblems(@Param("studentId") Long studentId,
                                                 @Param("examSheetId") Long examSheetId);
    void deleteAnswers(@Param("studentId") Long studentId, @Param("examSheetId") Long examSheetId);
    void insertAnswers(@Param("answers") List<StudentAnswer> answers);
    List<TagAccuracyDto> findAccuracyByTagType(@Param("studentId") Long studentId,
                                                @Param("tagType") String tagType);
}
