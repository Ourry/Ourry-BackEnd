package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Solution;
import com.bluewhaletech.Ourry.domain.VoteId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SolutionRepository {
    @PersistenceContext
    private EntityManager em;

    public VoteId save(Solution solution) {
        if(solution.getVoteId() == null) {
            em.persist(solution);
        } else {
            em.merge(solution);
        }
        return solution.getVoteId();
    }
}
