package com.resume.builder.resume_builder.chatbot;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.builder.resume_builder.services.OllamaService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private OllamaService ollamaService;
    
    @PostMapping("/resume-score")
    public ResponseEntity<Map<String, Object>> analyzeResume(@RequestBody Map<String, Object> resumeData) {
        String resumeText = "";
        
        try {
            resumeText = new ObjectMapper().writeValueAsString(resumeData); // Convert JSON to String
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process resume JSON"));
        }

        // Pass resumeText to ATS optimization method
        String analysisResult = ollamaService.optimizeForATS(resumeText, "Job Description Placeholder");

        Map<String, Object> response = new HashMap<>();
        response.put("analysis", analysisResult);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/message")
    public ResponseEntity<String> handleChat(@RequestBody Map<String, String> requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Request body is missing or empty.");
        }

        String userInput = requestBody.get("userInput");
        String resumeText = requestBody.get("resumeText"); // Optional
        String jobDescription = requestBody.get("jobDescription"); // Optional

        if (userInput == null || userInput.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: userInput is required.");
        }

        if (userInput.toLowerCase().contains("optimize my resume")) {
            if (resumeText == null || jobDescription == null) {
                return ResponseEntity.badRequest().body("Error: Resume text and job description are required for optimization.");
            }
            return ResponseEntity.ok(ollamaService.optimizeForATS(resumeText, jobDescription));
        }

        return ResponseEntity.ok(ollamaService.chatWithOllama(userInput, resumeText));
    }
}
