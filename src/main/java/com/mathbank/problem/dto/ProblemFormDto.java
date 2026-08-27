package com.mathbank.problem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemFormDto {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "문제 내용을 입력하세요.")
    private String content;

    @NotBlank(message = "정답을 입력하세요.")
    private String answer;

    private String explanation;

    private List<Long> tagIds;

    private MultipartFile image;

    private boolean removeImage;
}
