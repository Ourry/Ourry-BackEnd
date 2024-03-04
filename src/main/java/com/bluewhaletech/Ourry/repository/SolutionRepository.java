package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Solution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SolutionRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Solution solution) {
        if(solution.getSolutionId() == null) {
            em.persist(solution);
        } else {
            em.merge(solution);
        }
    }
}
