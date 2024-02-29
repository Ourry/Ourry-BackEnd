package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Question;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ArticleRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Question question) {
        if(question.getQuestionId() == null) {
            em.persist(question);
        } else {
            em.merge(question);
        }
        return question.getQuestionId();
    }

    public Optional<Question> findAll() {
        return Optional.ofNullable(
                em.createQuery("select q from Question q", Question.class).getSingleResult()
        );
    }
}
