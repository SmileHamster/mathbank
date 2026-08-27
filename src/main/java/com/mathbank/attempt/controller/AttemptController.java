package com.mathbank.attempt.controller;

import com.mathbank.attempt.domain.Student;
import com.mathbank.attempt.dto.AttemptProblemDto;
import com.mathbank.attempt.dto.StudentAttemptSummaryDto;
import com.mathbank.attempt.dto.StudentFormDto;
import com.mathbank.attempt.service.AttemptService;
import com.mathbank.attempt.service.StudentService;
import com.mathbank.auth.mapper.MemberMapper;
import com.mathbank.examsheet.dto.ExamSheetListDto;
import com.mathbank.examsheet.service.ExamSheetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/attempt")
@RequiredArgsConstructor
public class AttemptController {

    private final StudentService studentService;
    private final AttemptService attemptService;
    private final ExamSheetService examSheetService;
    private final MemberMapper memberMapper;

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.getStudentList());
        return "attempt/student-list";
    }

    @GetMapping("/students/new")
    public String newForm(Model model) {
        model.addAttribute("form", new StudentFormDto());
        return "attempt/student-form";
    }

    @PostMapping("/students/new")
    public String create(@Valid @ModelAttribute("form") StudentFormDto form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "attempt/student-form";
        }
        Long id = studentService.createStudent(form);
        return "redirect:/attempt/students/" + id;
    }

    @GetMapping("/students/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Student student = studentService.getStudent(id);
        List<StudentAttemptSummaryDto> summaries = attemptService.getAttemptSummaries(id);
        model.addAttribute("student", student);
        model.addAttribute("summaries", summaries);
        return "attempt/student-detail";
    }

    @GetMapping("/students/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudent(id);
        StudentFormDto form = new StudentFormDto();
        form.setName(student.getName());
        form.setGrade(student.getGrade());
        form.setMemo(student.getMemo());
        model.addAttribute("studentId", id);
        model.addAttribute("form", form);
        return "attempt/student-form";
    }

    @PostMapping("/students/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") StudentFormDto form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("studentId", id);
            return "attempt/student-form";
        }
        studentService.updateStudent(id, form);
        return "redirect:/attempt/students/" + id;
    }

    @PostMapping("/students/{id}/delete")
    public String delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/attempt/students";
    }

    @GetMapping("/students/{id}/record")
    public String recordSelect(@PathVariable Long id, Model model) {
        Student student = studentService.getStudent(id);
        Long memberId = getMemberId();
        List<ExamSheetListDto> examSheets = examSheetService.getExamSheetList(memberId);
        model.addAttribute("student", student);
        model.addAttribute("examSheets", examSheets);
        return "attempt/record-select";
    }

    @GetMapping("/students/{id}/record/{examSheetId}")
    public String recordForm(@PathVariable Long id, @PathVariable Long examSheetId, Model model) {
        Student student = studentService.getStudent(id);
        List<AttemptProblemDto> problems = attemptService.getAttemptProblems(id, examSheetId);
        model.addAttribute("student", student);
        model.addAttribute("examSheetId", examSheetId);
        model.addAttribute("problems", problems);
        return "attempt/record-form";
    }

    @PostMapping("/students/{id}/record/{examSheetId}")
    public String recordSave(@PathVariable Long id, @PathVariable Long examSheetId,
                             @RequestParam List<Long> allProblemIds,
                             @RequestParam(required = false) List<Long> correctProblemIds) {
        attemptService.saveAttempt(id, examSheetId, allProblemIds, correctProblemIds);
        return "redirect:/attempt/students/" + id;
    }

    private Long getMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return memberMapper.findByUsername(auth.getName()).getId();
    }
}
