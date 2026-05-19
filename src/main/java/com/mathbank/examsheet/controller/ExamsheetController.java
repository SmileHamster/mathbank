package com.mathbank.examsheet.controller;

import com.mathbank.auth.mapper.MemberMapper;
import com.mathbank.examsheet.dto.ExamSheetCreateDto;
import com.mathbank.examsheet.dto.ExamSheetDetailDto;
import com.mathbank.examsheet.dto.ExamSheetListDto;
import com.mathbank.examsheet.service.ExamSheetService;
import com.mathbank.problem.domain.Tag;
import com.mathbank.problem.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/examsheet")
@RequiredArgsConstructor
public class ExamsheetController {

    private final ExamSheetService examSheetService;
    private final TagService tagService;
    private final MemberMapper memberMapper;

    @GetMapping("/list")
    public String list(Model model) {
        Long memberId = getMemberId();
        List<ExamSheetListDto> examSheets = examSheetService.getExamSheetList(memberId);
        model.addAttribute("examSheets", examSheets);
        return "examsheet/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ExamSheetCreateDto());
        addTagGroups(model);
        return "examsheet/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") ExamSheetCreateDto form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addTagGroups(model);
            return "examsheet/form";
        }
        try {
            Long memberId = getMemberId();
            Long id = examSheetService.createExamSheet(form, memberId);
            return "redirect:/examsheet/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addTagGroups(model);
            return "examsheet/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ExamSheetDetailDto detail = examSheetService.getExamSheetDetail(id);
        model.addAttribute("examSheet", detail);
        return "examsheet/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        examSheetService.deleteExamSheet(id);
        return "redirect:/examsheet/list";
    }

    private Long getMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return memberMapper.findByUsername(auth.getName()).getId();
    }

    private void addTagGroups(Model model) {
        Map<String, List<Tag>> tagGroups = tagService.getTagsGroupedByType();
        model.addAttribute("gradeTags",      tagGroups.getOrDefault("GRADE",      List.of()));
        model.addAttribute("semesterTags",   tagGroups.getOrDefault("SEMESTER",   List.of()));
        model.addAttribute("unitTags",       tagGroups.getOrDefault("UNIT",       List.of()));
        model.addAttribute("difficultyTags", tagGroups.getOrDefault("DIFFICULTY", List.of()));
    }
}
