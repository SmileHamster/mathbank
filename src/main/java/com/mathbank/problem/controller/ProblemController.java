package com.mathbank.problem.controller;

import com.mathbank.auth.mapper.MemberMapper;
import com.mathbank.problem.domain.Tag;
import com.mathbank.problem.dto.ProblemDetailDto;
import com.mathbank.problem.dto.ProblemFormDto;
import com.mathbank.problem.dto.ProblemSearchDto;
import com.mathbank.problem.service.ProblemService;
import com.mathbank.problem.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final TagService tagService;
    private final MemberMapper memberMapper;

    @GetMapping("/list")
    public String list(@ModelAttribute ProblemSearchDto searchDto, Model model) {
        Map<String, Object> result = problemService.getProblemList(searchDto);
        model.addAttribute("problems", result.get("problems"));
        model.addAttribute("pageInfo", result.get("pageInfo"));
        model.addAttribute("searchDto", result.get("searchDto"));
        model.addAttribute("tagGroups", tagService.getTagsGroupedByType());
        return "problem/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProblemFormDto());
        model.addAttribute("tagGroups", tagService.getTagsGroupedByType());
        return "problem/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") ProblemFormDto form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tagGroups", tagService.getTagsGroupedByType());
            return "problem/form";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = memberMapper.findByUsername(auth.getName()).getId();
        problemService.createProblem(form, memberId);
        return "redirect:/problem/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("problem", problemService.getProblemDetail(id));
        return "problem/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProblemDetailDto problem = problemService.getProblemDetail(id);

        ProblemFormDto form = new ProblemFormDto();
        form.setTitle(problem.getTitle());
        form.setContent(problem.getContent());
        form.setAnswer(problem.getAnswer());
        form.setExplanation(problem.getExplanation());
        form.setTagIds(problem.getTagList().stream().map(Tag::getId).collect(Collectors.toList()));

        Set<Long> selectedTagIds = problem.getTagList().stream()
                .map(Tag::getId).collect(Collectors.toSet());

        model.addAttribute("problem", problem);
        model.addAttribute("form", form);
        model.addAttribute("selectedTagIds", selectedTagIds);
        model.addAttribute("tagGroups", tagService.getTagsGroupedByType());
        return "problem/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") ProblemFormDto form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            ProblemDetailDto problem = problemService.getProblemDetail(id);
            Set<Long> selectedTagIds = problem.getTagList().stream()
                    .map(Tag::getId).collect(Collectors.toSet());
            model.addAttribute("problem", problem);
            model.addAttribute("selectedTagIds", selectedTagIds);
            model.addAttribute("tagGroups", tagService.getTagsGroupedByType());
            return "problem/form";
        }
        problemService.updateProblem(id, form);
        return "redirect:/problem/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return "redirect:/problem/list";
    }
}
