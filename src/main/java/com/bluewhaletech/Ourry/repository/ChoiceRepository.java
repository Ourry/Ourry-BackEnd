package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Question;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ChoiceRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Choice choice) {
        if(choice.getChoiceId() == null) {
            em.persist(choice);
        } else {
            em.merge(choice);
        }
    }

    public Choice findByQuestionAndSequence(Question question, int sequence) {
        TypedQuery<Choice> query = em.createQuery("select c from Choice c where c.question =: question and c.sequence =: sequence", Choice.class)
                .setParameter("question", question)
                .setParameter("sequence", sequence);
        return query.getSingleResult();
    }
}