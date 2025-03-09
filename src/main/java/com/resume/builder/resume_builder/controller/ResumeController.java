package com.resume.builder.resume_builder.controller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.builder.resume_builder.model.ResumeRequest;
import com.resume.builder.resume_builder.resume.ResumeService;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final RestTemplate restTemplate;

    public ResumeController(ResumeService resumeService, RestTemplate restTemplate) {
        this.resumeService = resumeService;
        this.restTemplate = restTemplate;
    }

    // Resume Scoring API
    @PostMapping("/score")
    public ResponseEntity<Map<String, Object>> getResumeScore(@Valid @RequestBody ResumeRequest resumeRequest) {
        Map<String, Object> response = new HashMap<>();
        int score = calculateResumeScore(resumeRequest);
        String feedback = generateFeedback(resumeRequest);

        response.put("score", score);
        response.put("feedback", feedback);
        return ResponseEntity.ok(response);
    }

    private int calculateResumeScore(ResumeRequest resumeRequest) {
        int score = 50; // Base score
        if (resumeRequest.getTechnicalSkills() != null) {
            score += resumeRequest.getTechnicalSkills().size() * 5;
        }
        if (resumeRequest.getWorkExperience() != null) {
            score += resumeRequest.getWorkExperience().size() * 10;
        }
        if (resumeRequest.getCertifications() != null) {
            score += resumeRequest.getCertifications().size() * 5;
        }
        return Math.min(score, 100);
    }

    private String generateFeedback(ResumeRequest resumeRequest) {
        if (resumeRequest.getTechnicalSkills() == null || resumeRequest.getTechnicalSkills().isEmpty()) {
            return "Consider adding more technical skills.";
        }
        if (resumeRequest.getWorkExperience() == null || resumeRequest.getWorkExperience().isEmpty()) {
            return "Consider adding more work experience.";
        }
        return "Your resume looks good! Consider tailoring it more to the job role.";
    }

    // Resume PDF Generation API
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateResume(@RequestBody ResumeRequest resumeRequest) {
        ByteArrayOutputStream pdfStream = resumeService.createResumePDF(resumeRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.toByteArray());
    }

    // Job-Specific Suggestions API
    @PostMapping("/suggestions")
    public Map<String, Object> getJobSpecificSuggestions(@RequestBody Map<String, String> request) {
        String jobDescription = request.get("jobDescription");
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("skills", generateSkills(jobDescription));
        suggestions.put("jobDescription", generateJobDescription(jobDescription));
        suggestions.put("achievements", generateAchievements(jobDescription));
        return suggestions;
    }

    private String[] generateSkills(String jobDescription) {
        if (jobDescription.toLowerCase().contains("java developer")) {
            return new String[]{"Java", "Spring Boot", "Hibernate", "Microservices", "REST APIs"};
        } else if (jobDescription.toLowerCase().contains("data scientist")) {
            return new String[]{"Python", "Machine Learning", "TensorFlow", "Pandas", "NLP"};
        }
        return new String[]{"Communication", "Problem-Solving", "Team Collaboration"};
    }

    private String generateJobDescription(String jobDescription) {
        if (jobDescription.toLowerCase().contains("java developer")) {
            return "Develop and maintain Java-based applications using Spring Boot and Microservices.";
        } else if (jobDescription.toLowerCase().contains("data scientist")) {
            return "Analyze and interpret complex data using machine learning algorithms to drive business insights.";
        }
        return "Analyze business needs and develop technical solutions.";
    }

    private String[] generateAchievements(String jobDescription) {
        if (jobDescription.toLowerCase().contains("java developer")) {
            return new String[]{"Optimized API performance by 30%", "Led a team of 5 developers in a microservices project"};
        } else if (jobDescription.toLowerCase().contains("data scientist")) {
            return new String[]{"Developed an AI model that improved prediction accuracy by 25%", "Built a chatbot using NLP"};
        }
        return new String[]{"Achieved project deadlines consistently", "Received positive feedback from stakeholders"};
    }

    // Ollama-based Sentiment Analysis API
    @PostMapping("/analyze-sentiment")
    public ResponseEntity<Map<String, Object>> analyzeResumeSentiment(@RequestBody ResumeRequest resumeRequest) {
        Map<String, Object> sentimentFeedback = new HashMap<>();
        sentimentFeedback.put("summaryFeedback", analyzeWithOllama("summary", resumeRequest.getSummary()));
        sentimentFeedback.put("workExperienceFeedback", analyzeWorkExperienceWithOllama(resumeRequest.getWorkExperience()));
        sentimentFeedback.put("projectsFeedback", analyzeWithOllama("projects", String.join(", ", resumeRequest.getProjects())));

        return ResponseEntity.ok(sentimentFeedback);
    }

    private String analyzeWithOllama(String section, String text) {
        if (text == null || text.isEmpty()) {
            return "No " + section + " provided. Consider adding this section.";
        }
        String prompt = "Analyze the sentiment of the following " + section + " in a resume and provide feedback:\n" + text;
        return callOllamaAPI(prompt);
    }

    private Map<String, String> analyzeWorkExperienceWithOllama(List<ResumeRequest.WorkExperience> workExperience) {
        Map<String, String> feedback = new HashMap<>();
        if (workExperience == null || workExperience.isEmpty()) {
            feedback.put("general", "No work experience provided. Consider adding internships or freelance work.");
            return feedback;
        }
        for (ResumeRequest.WorkExperience experience : workExperience) {
            String prompt = "Analyze the sentiment of this job experience: " + experience.getJobTitle() +
                    ". Responsibilities: " + String.join(", ", experience.getResponsibilities());
            feedback.put(experience.getJobTitle(), callOllamaAPI(prompt));
        }
        return feedback;
    }

    private String callOllamaAPI(String prompt) {
        String ollamaUrl = "http://localhost:11434/api/generate";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "mistral");
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);  // Ensure non-streaming response

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(ollamaUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Convert response JSON string into Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                
                return responseBody.get("response").toString();
            } else {
                System.err.println("Ollama API Error: " + response);
                return "Error analyzing sentiment.";
            }
        } catch (Exception e) {
            System.err.println("Exception while calling Ollama API: " + e.getMessage());
            return "Error analyzing sentiment.";
        }
    }

}
