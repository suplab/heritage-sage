package com.heritage.sage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heritage.sage.model.EvaluationRecord;
import com.heritage.sage.repository.EvaluationRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.io.InputStream;

@Service
public class EvaluationService {

    @Value("${eval.service.url:http://localhost:8001/evaluate-image}")
    private String evalServiceUrl;

    @Autowired
    private EvaluationRepository evaluationRepository;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public EvaluationRecord evaluateFromUrls(String skillName, String learnerId, String imageUrl, String referenceUrl) throws Exception {
        byte[] imageBytes = fetchUrlBytes(imageUrl);
        byte[] refBytes = fetchUrlBytes(referenceUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "upload.jpg";
            }
        });
        body.add("reference", new ByteArrayResource(refBytes) {
            @Override
            public String getFilename() {
                return "reference.jpg";
            }
        });

        HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> resp = rest.postForEntity(evalServiceUrl, requestEntity, String.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                JsonNode node = mapper.readTree(resp.getBody());
                double score = node.has("score") ? node.get("score").asDouble() : 0.0;
                String feedback = node.has("feedback") ? node.get("feedback").asText() : "";
                EvaluationRecord rec = new EvaluationRecord(skillName, learnerId, score, feedback);
                evaluationRepository.save(rec);
                return rec;
            } else {
                throw new RuntimeException("Eval service returned status: " + resp.getStatusCode());
            }
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Eval service error: " + ex.getStatusText() + " body:" + ex.getResponseBodyAsString());
        }
    }

    private byte[] fetchUrlBytes(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        try (InputStream in = url.openStream()) {
            return in.readAllBytes();
        }
    }
}
