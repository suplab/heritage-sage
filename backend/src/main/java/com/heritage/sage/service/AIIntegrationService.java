package com.heritage.sage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Direct REST fallback — used only when LangChain4j returns null (no API key).
 * Respects the same AI_PROVIDER selection as LangChainLessonService.
 */
@Service
public class AIIntegrationService {

    @Value("${ai.provider:groq}")
    private String provider;

    @Value("${ai.groq.api-key:}")
    private String groqApiKey;

    @Value("${ai.groq.model:llama-3.3-70b-versatile}")
    private String groqModel;

    @Value("${ai.huggingface.api-key:}")
    private String hfApiKey;

    @Value("${ai.huggingface.model:meta-llama/Llama-3.2-3B-Instruct}")
    private String hfModel;

    @Value("${ai.openai.api-key:}")
    private String openAiKey;

    @Value("${ai.openai.model:gpt-4o-mini}")
    private String openAiModel;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String queryAI(String prompt) {
        String apiKey = resolvedKey();
        if (apiKey == null || apiKey.isBlank()) {
            return "MOCK LESSON: " + prompt + "\n\n(Set GROQ_API_KEY / HUGGINGFACE_API_KEY / OPENAI_API_KEY to enable real AI responses.)";
        }
        String baseUrl = resolvedBaseUrl();
        String model   = resolvedModel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", new Object[]{Map.of("role", "user", "content", prompt)},
                "max_tokens", 600
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> resp = rest.postForEntity(baseUrl + "/chat/completions", entity, String.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                JsonNode root = mapper.readTree(resp.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            } else {
                return "ERROR from AI provider: " + resp.getStatusCode();
            }
        } catch (Exception e) {
            return "ERROR calling AI provider: " + e.getMessage();
        }
    }

    private String resolvedKey() {
        return switch (provider.toLowerCase()) {
            case "groq" -> groqApiKey;
            case "huggingface" -> hfApiKey;
            default -> openAiKey;
        };
    }

    private String resolvedBaseUrl() {
        return switch (provider.toLowerCase()) {
            case "groq" -> "https://api.groq.com/openai/v1";
            case "huggingface" -> "https://api-inference.huggingface.co/v1";
            default -> "https://api.openai.com/v1";
        };
    }

    private String resolvedModel() {
        return switch (provider.toLowerCase()) {
            case "groq" -> groqModel;
            case "huggingface" -> hfModel;
            default -> openAiModel;
        };
    }
}
