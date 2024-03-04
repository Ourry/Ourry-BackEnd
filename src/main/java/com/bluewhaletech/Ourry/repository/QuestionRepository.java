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

    public Optional<List<Question>> findAll() {
        TypedQuery<Question> query = em.createQuery("select q from Question q", Question.class);
        return Optional.ofNullable(query.getResultList());
    }

    public Optional<Question> findOne(Long questionId) {
        return Optional.ofNullable(em.find(Question.class, questionId));
    }

    public Optional<Question> findByMemberId(Long memberId) {
        return Optional.ofNullable(
                em.createQuery("select q from Question q where member_id =:memberId", Question.class)
                        .setParameter("member_id", memberId)
                        .getSingleResult()
        );
    }
}