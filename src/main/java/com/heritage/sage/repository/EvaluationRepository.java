package com.heritage.sage.repository;

import com.heritage.sage.model.EvaluationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationRecord, Long> {
    List<EvaluationRecord> findBySkillName(String skillName);
}
