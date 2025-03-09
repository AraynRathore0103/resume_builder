package com.resume.builder.resume_builder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.resume.builder.resume_builder.services.OllamaService;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class Controller {
    @Autowired
    private OllamaService ollamaService;
    @PostMapping
    public String chat(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");  
        String userInput = requestBody.get("message");
        String resumeText = requestBody.getOrDefault("resumeText", ""); // Handle missing resumeText

        return ollamaService.chatWithOllama(userInput, resumeText);
    }

}
