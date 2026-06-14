package com.heritage.sage.repository;

import com.heritage.sage.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findBySkillName(String skillName);
}
