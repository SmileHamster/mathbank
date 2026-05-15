package com.mathbank.examsheet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/examsheet")
public class ExamsheetController {

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("pageTitle", "시험지 관리");
        return "common/coming-soon";
    }
}
