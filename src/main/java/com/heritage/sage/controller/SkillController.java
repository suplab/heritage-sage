package com.heritage.sage.controller;

import com.heritage.sage.model.Skill;
import com.heritage.sage.repository.SkillRepository;
import com.heritage.sage.service.LessonService;
import com.heritage.sage.service.EvaluationService;
import com.heritage.sage.service.FeedbackAgentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private FeedbackAgentService feedbackAgentService;

    @GetMapping
    public List<Skill> list() {
        return skillRepository.findAll();
    }

    @PostMapping
    public Skill create(@RequestBody Skill s) {
        return skillRepository.save(s);
    }

    @GetMapping("/{name}/lesson")
    public ResponseEntity<String> generateLesson(@PathVariable String name, @RequestParam(defaultValue = "beginner") String level) {
        String lesson = lessonService.generateLesson(name, level);
        return ResponseEntity.ok(lesson);
    }

    // Trigger evaluation by providing public URLs for the learner image and reference image
    @PostMapping("/{name}/evaluate")
    public ResponseEntity<?> evaluateSkill(@PathVariable String name, @RequestBody java.util.Map<String,String> payload) {
        try {
            String learnerId = payload.getOrDefault("learnerId", "anonymous");
            String imageUrl = payload.get("imageUrl");
            String referenceUrl = payload.get("referenceUrl");
            if (imageUrl == null || referenceUrl == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "imageUrl and referenceUrl required"));
            }
            com.heritage.sage.model.EvaluationRecord rec = evaluationService.evaluateFromUrls(name, learnerId, imageUrl, referenceUrl);
            return ResponseEntity.ok(rec);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Generate adaptive feedback for a specific evaluation id
    @GetMapping("/evaluation/{id}/adaptive-feedback")
    public ResponseEntity<?> adaptiveFeedback(@PathVariable Long id) {
        var opt = feedbackAgentService.getEvaluationRepository().findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", "evaluation not found"));
        }
        com.heritage.sage.model.EvaluationRecord rec = opt.get();
        String feedback = feedbackAgentService.generateAdaptiveFeedback(rec);
        return ResponseEntity.ok(java.util.Map.of("adaptiveFeedback", feedback));
    }
}
