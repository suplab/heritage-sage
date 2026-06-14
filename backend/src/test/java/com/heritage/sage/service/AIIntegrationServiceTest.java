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
        ReflectionTestUtils.setField(service, "openAiKey", "");
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
        assertThat(result).contains("OPENAI_API_KEY");
    }
}
