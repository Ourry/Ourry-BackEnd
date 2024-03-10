package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Question;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository {
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

    public List<Question> findAll() {
        TypedQuery<Question> query = em.createQuery("select q from Question q", Question.class);
        return query.getResultList();
    }

    public Question findOne(Long questionId) {
        return em.find(Question.class, questionId);
    }
}