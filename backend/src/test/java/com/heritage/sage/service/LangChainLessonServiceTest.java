package com.heritage.sage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class LangChainLessonServiceTest {

    private LangChainLessonService service;

    @BeforeEach
    void setUp() {
        service = new LangChainLessonService();
        ReflectionTestUtils.setField(service, "provider", "groq");
        ReflectionTestUtils.setField(service, "groqApiKey", "");
        ReflectionTestUtils.setField(service, "groqModel", "llama-3.3-70b-versatile");
        ReflectionTestUtils.setField(service, "hfApiKey", "");
        ReflectionTestUtils.setField(service, "hfModel", "meta-llama/Llama-3.2-3B-Instruct");
        ReflectionTestUtils.setField(service, "openAiKey", "");
        ReflectionTestUtils.setField(service, "openAiModel", "gpt-4o-mini");
        service.init();
    }

    @Test
    void generateLesson_returnsNull_whenApiKeyBlank() {
        String result = service.generateLessonWithLangChain("Calligraphy", "beginner");
        assertThat(result).isNull();
    }

    @Test
    void callAI_returnsNull_whenModelNotInitialized() {
        String result = service.callAI("some prompt");
        assertThat(result).isNull();
    }

    @Test
    void init_supportsGroqProvider() {
        LangChainLessonService svc = new LangChainLessonService();
        ReflectionTestUtils.setField(svc, "provider", "groq");
        ReflectionTestUtils.setField(svc, "groqApiKey", "");
        ReflectionTestUtils.setField(svc, "groqModel", "llama-3.3-70b-versatile");
        ReflectionTestUtils.setField(svc, "hfApiKey", "");
        ReflectionTestUtils.setField(svc, "hfModel", "meta-llama/Llama-3.2-3B-Instruct");
        ReflectionTestUtils.setField(svc, "openAiKey", "");
        ReflectionTestUtils.setField(svc, "openAiModel", "gpt-4o-mini");
        svc.init();
        assertThat(svc.callAI("prompt")).isNull(); // no key → null
    }

    @Test
    void init_supportsHuggingFaceProvider() {
        LangChainLessonService svc = new LangChainLessonService();
        ReflectionTestUtils.setField(svc, "provider", "huggingface");
        ReflectionTestUtils.setField(svc, "groqApiKey", "");
        ReflectionTestUtils.setField(svc, "groqModel", "llama-3.3-70b-versatile");
        ReflectionTestUtils.setField(svc, "hfApiKey", "");
        ReflectionTestUtils.setField(svc, "hfModel", "meta-llama/Llama-3.2-3B-Instruct");
        ReflectionTestUtils.setField(svc, "openAiKey", "");
        ReflectionTestUtils.setField(svc, "openAiModel", "gpt-4o-mini");
        svc.init();
        assertThat(svc.callAI("prompt")).isNull();
    }
}
