package com.heritage.sage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonService {

    @Autowired
    private AIIntegrationService aiIntegrationService;

    @Autowired
    private LangChainLessonService langChainLessonService;

    public String generateLesson(String skillName, String level) {
        try {
            String lc = langChainLessonService.generateLessonWithLangChain(skillName, level);
            if (lc != null && !lc.isEmpty()) {
                return lc;
            }
        } catch (Exception e) {
            // fall back
        }
        String prompt = String.format("Create a concise %s lesson for the skill '%s'. Include a short practice exercise and expected outcomes.", level, skillName);
        return aiIntegrationService.queryAI(prompt);
    }
}
