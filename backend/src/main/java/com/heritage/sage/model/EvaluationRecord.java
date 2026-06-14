package com.heritage.sage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
public class EvaluationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;
    private String learnerId;
    private double score;
    @Column(columnDefinition = "text")
    private String feedback;
    private LocalDateTime evaluatedAt;

    public EvaluationRecord() {}

    public EvaluationRecord(String skillName, String learnerId, double score, String feedback) {
        this.skillName = skillName;
        this.learnerId = learnerId;
        this.score = score;
        this.feedback = feedback;
        this.evaluatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
