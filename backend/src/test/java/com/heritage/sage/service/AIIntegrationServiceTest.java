package com.heritage.sage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class AIIntegrationServiceTest {

    private AIIntegrationService service;

    @BeforeEach
    void setUp() {
        service = new AIIntegrationService();
        ReflectionTestUtils.setField(service, "provider", "groq");
        ReflectionTestUtils.setField(service, "groqApiKey", "");
        ReflectionTestUtils.setField(service, "hfApiKey", "");
        ReflectionTestUtils.setField(service, "openAiKey", "");
        ReflectionTestUtils.setField(service, "groqModel", "llama-3.3-70b-versatile");
        ReflectionTestUtils.setField(service, "hfModel", "meta-llama/Llama-3.2-3B-Instruct");
        ReflectionTestUtils.setField(service, "openAiModel", "gpt-4o-mini");
    }

    @Test
    void queryAI_returnsMock_whenApiKeyBlank() {
        String result = service.queryAI("test prompt");
        assertThat(result).startsWith("MOCK LESSON:");
        assertThat(result).contains("test prompt");
    }

    @Test
    void queryAI_mockContainsConfigurationHint() {
        String result = service.queryAI("any prompt");
        assertThat(result).containsAnyOf("GROQ_API_KEY", "HUGGINGFACE_API_KEY", "OPENAI_API_KEY");
    }
}
