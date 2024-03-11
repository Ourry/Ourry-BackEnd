package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Poll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PollRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Poll poll) {
        if(poll.getPollId() == null) {
            em.persist(poll);
        } else {
            em.merge(poll);
        }
        return poll.getPollId();
    }
}