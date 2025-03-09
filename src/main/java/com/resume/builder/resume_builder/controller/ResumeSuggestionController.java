package com.resume.builder.resume_builder.controller;

import com.resume.builder.resume_builder.services.OllamaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
public class ResumeSuggestionController {

    private final OllamaService ollamaService;

    public ResumeSuggestionController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/suggestions")
    public String getResumeSuggestions(@RequestBody String jobDescription) {
        return ollamaService.getResumeSuggestions(jobDescription);
    }

    @PostMapping("/optimize")
    public String optimizeResume(@RequestParam String resumeText, @RequestParam String jobDescription) {
        return ollamaService.optimizeForATS(resumeText, jobDescription);
    }
}
