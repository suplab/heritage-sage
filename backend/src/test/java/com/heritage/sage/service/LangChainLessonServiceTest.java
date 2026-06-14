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
        ReflectionTestUtils.setField(service, "openAiKey", "");
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
}
