package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Question;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceJpaRepository extends org.springframework.data.repository.Repository<Choice, Long> {
    boolean existsByQuestionAndSequence(Question question, int sequence);
}