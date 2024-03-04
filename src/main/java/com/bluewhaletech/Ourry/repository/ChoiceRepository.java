package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    public Optional<Choice> findOne(Long choiceId) {
        return Optional.ofNullable(em.find(Choice.class, choiceId));
    }
}