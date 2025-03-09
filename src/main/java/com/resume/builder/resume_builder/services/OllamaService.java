package com.resume.builder.resume_builder.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate"; // Ollama API URL
    private static final String MODEL_NAME = "mistral"; // AI Model used

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 🔹 Conversational Resume Assistant (Chatbot)
     */
    public String chatWithOllama(String userInput, String resumeText) {
        if (userInput.toLowerCase().contains("optimize my resume")) {
            return optimizeForATS(resumeText, "Provide the job description for optimization.");
        }

        String systemPrompt = "You are a professional resume-building assistant.\n" +
                "User Input: " + userInput + "\nAssistant:";

        return sendOllamaRequest(systemPrompt);
    }

    /**
     * 🔹 Job-Specific Resume Suggestions (Returns JSON String)
     */
    public String getResumeSuggestions(String jobDescription) {
        String prompt = "Suggest key skills, a job description, and achievements for this job: " + jobDescription;
        String response = sendOllamaRequest(prompt);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode == null || jsonNode.isEmpty()) {
                return "{\"error\": \"Invalid response from Ollama.\"}";
            }

            Map<String, String> resumeData = new HashMap<>();
            resumeData.put("Skills", jsonNode.path("skills").asText("Not provided"));
            resumeData.put("Job Description", jsonNode.path("job_description").asText("Not provided"));
            resumeData.put("Achievements", jsonNode.path("achievements").asText("Not provided"));

            return objectMapper.writeValueAsString(resumeData);
        } catch (IOException e) {
            return "{\"error\": \"Failed to parse resume suggestions.\"}";
        }
    }

    /**
     * 🔹 ATS Optimization for Resumes
     */
    public String optimizeForATS(String resumeText, String jobDescription) {
        if (resumeText == null || resumeText.isEmpty() || jobDescription == null || jobDescription.isEmpty()) {
            return "Error: Resume text and job description cannot be empty.";
        }

        String prompt = "Analyze the following resume and job description:\n\n" +
                "Resume: " + resumeText + "\n\n" +
                "Job Description: " + jobDescription + "\n\n" +
                "Provide an ATS compatibility score (0-100) and suggest missing keywords " +
                "and improvements to enhance ATS ranking. Also, suggest formatting changes " +
                "to improve readability for applicant tracking systems.";

        return sendOllamaRequest(prompt);
    }
    
    /**
     * 🔹 Resume Scoring System
     * Evaluates a resume based on content quality, completeness, and keyword optimization.
     */
    public String evaluateResume(String resumeText, String jobDescription) {
        String prompt = "Evaluate the following resume for content quality, completeness, and keyword optimization:\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Job Description:\n" + jobDescription + "\n\n" +
                "Provide a numerical score (0-100) along with feedback on missing sections, keyword optimization, " +
                "and readability improvements.";

        return sendOllamaRequest(prompt);
    }


    /**
     * 🔹 Sends the request to Ollama API using a proper Response Handler
     */
    private String sendOllamaRequest(String prompt) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(OLLAMA_API_URL);
            request.setHeader("Content-Type", "application/json");

            // Construct JSON payload properly using ObjectMapper
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", MODEL_NAME);
            payload.put("prompt", prompt);
            payload.put("stream", false);

            String jsonPayload = objectMapper.writeValueAsString(payload);
            request.setEntity(new StringEntity(jsonPayload));

            HttpClientResponseHandler<String> responseHandler = (ClassicHttpResponse response) -> {
                int status = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (status >= 200 && status < 300) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);

                    if (jsonResponse == null || !jsonResponse.has("response")) {
                        return "Error: Unexpected response format from Ollama.";
                    }
                    return jsonResponse.get("response").asText();
                } else {
                    return "Unexpected response status: " + status + " - " + responseBody;
                }
            };

            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            return "Error: Unable to connect to Ollama API - " + e.getMessage();
        }
        
    }
}
