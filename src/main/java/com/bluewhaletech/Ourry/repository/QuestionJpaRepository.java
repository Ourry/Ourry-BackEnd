package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Category;
import com.bluewhaletech.Ourry.domain.Question;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionJpaRepository extends org.springframework.data.repository.Repository<Question, Long> {
    List<Question> findByCategory(Category category);
}
