package com.mathbank.attempt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attempt")
public class AttemptController {

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("pageTitle", "학생 관리");
        return "common/coming-soon";
    }
}
