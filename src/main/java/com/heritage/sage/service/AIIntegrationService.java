package com.heritage.sage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AIIntegrationService {

    @Value("${openai.api.key:}")
    private String openAiKey;

    private final RestTemplate rest = new RestTemplate();

    public String queryAI(String prompt) {
        if (openAiKey == null || openAiKey.isEmpty()) {
            return "MOCK LESSON: " + prompt + "\n\n(Configure OPENAI_API_KEY to enable real AI responses.)";
        }

        String url = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);
        Map<String,Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[] { Map.of("role","user","content", prompt) },
                "max_tokens", 600
        );
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = rest.postForEntity(url, entity, String.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return resp.getBody();
        } else {
            return "ERROR from AI provider: " + resp.getStatusCode();
        }
    }
}
