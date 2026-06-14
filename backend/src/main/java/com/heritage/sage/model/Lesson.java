package com.heritage.sage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;
    private String level;

    @Column(columnDefinition = "text")
    private String content;

    private LocalDateTime generatedAt;

    public Lesson() {}

    public Lesson(String skillName, String level, String content, LocalDateTime generatedAt) {
        this.skillName = skillName;
        this.level = level;
        this.content = content;
        this.generatedAt = generatedAt;
    }

    public Long getId() { return id; }
    public String getSkillName() { return skillName; }
    public String getLevel() { return level; }
    public String getContent() { return content; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}
