package com.heritage.sage.service;

import com.heritage.sage.model.Lesson;
import com.heritage.sage.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LessonService {

    @Autowired
    private AIIntegrationService aiIntegrationService;

    @Autowired
    private LangChainLessonService langChainLessonService;

    @Autowired
    private LessonRepository lessonRepository;

    public String generateLesson(String skillName, String level) {
        String content;
        try {
            String lc = langChainLessonService.generateLessonWithLangChain(skillName, level);
            if (lc != null && !lc.isBlank()) {
                content = lc;
            } else {
                String prompt = String.format(
                        "Create a concise %s lesson for the skill '%s'. Include a short practice exercise and expected outcomes.",
                        level, skillName);
                content = aiIntegrationService.queryAI(prompt);
            }
        } catch (Exception e) {
            String prompt = String.format(
                    "Create a concise %s lesson for the skill '%s'. Include a short practice exercise and expected outcomes.",
                    level, skillName);
            content = aiIntegrationService.queryAI(prompt);
        }

        Lesson lesson = new Lesson(skillName, level, content, LocalDateTime.now());
        lessonRepository.save(lesson);
        return content;
    }
}
