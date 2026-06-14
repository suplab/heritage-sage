package com.heritage.sage.service;

import com.heritage.sage.model.EvaluationRecord;
import com.heritage.sage.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackAgentService {

    @Autowired
    private LangChainLessonService langChainLessonService;

    @Autowired
    private EvaluationRepository evaluationRepository;

    public EvaluationRepository getEvaluationRepository() { return this.evaluationRepository; }

    public String generateAdaptiveFeedback(EvaluationRecord record) {
        String prompt = String.format(
                "You are an encouraging mentor. A learner submitted work for skill '%s'.\n" +
                "Score: %.2f\nFeedback from evaluator: %s\n" +
                "Based on this, provide: (1) concise encouraging feedback, " +
                "(2) one short practice exercise to improve a specific weakness, " +
                "and (3) suggested difficulty adjustment (stay/simplify/advance).",
                record.getSkillName(), record.getScore(), record.getFeedback());

        String result = langChainLessonService.callAI(prompt);
        if (result != null && !result.isBlank()) {
            return result;
        }

        // Rule-based fallback when AI is unavailable
        String advice = record.getScore() > 0.75
                ? "Great job! Try a slightly more complex pattern next."
                : "Focus on accuracy; repeat the basic strokes slowly.";
        String exercise = record.getScore() > 0.75
                ? "Combine two previous exercises into a short piece."
                : "Practice the primary stroke for 10 minutes focusing on consistency.";
        String suggestion = record.getScore() > 0.75 ? "advance" : "stay";
        return String.format("Feedback: %s\nExercise: %s\nSuggestion: %s", advice, exercise, suggestion);
    }
}
