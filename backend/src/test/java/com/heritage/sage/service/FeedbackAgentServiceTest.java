package com.heritage.sage.service;

import com.heritage.sage.model.EvaluationRecord;
import com.heritage.sage.repository.EvaluationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackAgentServiceTest {

    @Mock
    private LangChainLessonService langChainLessonService;

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private FeedbackAgentService feedbackAgentService;

    private EvaluationRecord record(double score) {
        return new EvaluationRecord("Calligraphy", "user1", score, "some evaluator feedback");
    }

    @Test
    void generateAdaptiveFeedback_usesAI_whenAvailable() {
        when(langChainLessonService.callAI(any())).thenReturn("AI generated feedback");

        String result = feedbackAgentService.generateAdaptiveFeedback(record(0.5));

        assertThat(result).isEqualTo("AI generated feedback");
    }

    @Test
    void generateAdaptiveFeedback_ruleBasedFallback_lowScore() {
        when(langChainLessonService.callAI(any())).thenReturn(null);

        String result = feedbackAgentService.generateAdaptiveFeedback(record(0.5));

        assertThat(result).contains("stay");
        assertThat(result).contains("Focus on accuracy");
    }

    @Test
    void generateAdaptiveFeedback_ruleBasedFallback_highScore() {
        when(langChainLessonService.callAI(any())).thenReturn(null);

        String result = feedbackAgentService.generateAdaptiveFeedback(record(0.9));

        assertThat(result).contains("advance");
        assertThat(result).contains("Great job");
    }
}
