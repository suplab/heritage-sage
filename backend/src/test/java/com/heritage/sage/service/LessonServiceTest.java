package com.heritage.sage.service;

import com.heritage.sage.model.Lesson;
import com.heritage.sage.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LangChainLessonService langChainLessonService;

    @Mock
    private AIIntegrationService aiIntegrationService;

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void generateLesson_usesLangChain_whenAvailable() {
        when(langChainLessonService.generateLessonWithLangChain("Calligraphy", "beginner"))
                .thenReturn("LangChain lesson content");

        String result = lessonService.generateLesson("Calligraphy", "beginner");

        assertThat(result).isEqualTo("LangChain lesson content");
        verify(aiIntegrationService, never()).queryAI(any());
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void generateLesson_fallsBackToAI_whenLangChainReturnsNull() {
        when(langChainLessonService.generateLessonWithLangChain(any(), any())).thenReturn(null);
        when(aiIntegrationService.queryAI(any())).thenReturn("AI fallback content");

        String result = lessonService.generateLesson("Weaving", "intermediate");

        assertThat(result).isEqualTo("AI fallback content");
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void generateLesson_persistsLesson() {
        when(langChainLessonService.generateLessonWithLangChain(any(), any())).thenReturn("content");

        lessonService.generateLesson("Pottery", "advanced");

        verify(lessonRepository).save(argThat(l ->
                "Pottery".equals(l.getSkillName()) &&
                "advanced".equals(l.getLevel()) &&
                "content".equals(l.getContent())));
    }
}
