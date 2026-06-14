package com.heritage.sage.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Supports three AI providers via OpenAI-compatible REST APIs:
 *   groq        → https://api.groq.com/openai/v1
 *   huggingface → https://api-inference.huggingface.co/v1
 *   openai      → https://api.openai.com/v1
 *
 * Set AI_PROVIDER env var (defaults to groq).
 */
@Service
public class LangChainLessonService {

    private static final Logger log = LoggerFactory.getLogger(LangChainLessonService.class);

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

    private ChatModel chatModel;

    @PostConstruct
    void init() {
        chatModel = switch (provider.toLowerCase()) {
            case "groq" -> buildModel(
                    groqApiKey,
                    "https://api.groq.com/openai/v1",
                    groqModel);
            case "huggingface" -> buildModel(
                    hfApiKey,
                    "https://api-inference.huggingface.co/v1",
                    hfModel);
            default -> buildModel(
                    openAiKey,
                    "https://api.openai.com/v1",
                    openAiModel);
        };
        if (chatModel != null) {
            log.info("AI provider initialised: {} (model: {})", provider, resolvedModel());
        } else {
            log.warn("No API key configured for provider '{}' — AI features will use mock fallback.", provider);
        }
    }

    private ChatModel buildModel(String apiKey, String baseUrl, String modelName) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(600)
                .build();
    }

    private String resolvedModel() {
        return switch (provider.toLowerCase()) {
            case "groq" -> groqModel;
            case "huggingface" -> hfModel;
            default -> openAiModel;
        };
    }

    public String generateLessonWithLangChain(String skillName, String level) {
        if (chatModel == null) {
            return null;
        }
        String prompt = String.format(
                "You are a skill revival mentor. Create a concise %s lesson for the traditional skill '%s'. " +
                "Include: (1) a brief introduction, (2) step-by-step instructions, (3) a short practice exercise, " +
                "and (4) expected outcomes.",
                level, skillName);
        return callAI(prompt);
    }

    String callAI(String prompt) {
        if (chatModel == null) {
            return null;
        }
        return chatModel.chat(prompt);
    }
}
