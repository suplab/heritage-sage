package com.heritage.sage.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LangChainLessonService {

    @Value("${openai.api.key:}")
    private String openAiKey;

    private ChatModel chatModel;

    @PostConstruct
    void init() {
        if (openAiKey != null && !openAiKey.isBlank()) {
            chatModel = OpenAiChatModel.builder()
                    .apiKey(openAiKey)
                    .modelName("gpt-4o-mini")
                    .maxTokens(600)
                    .build();
        }
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
