package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Solution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SolutionRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Solution solution) {
        if(solution.getSolutionId() == null) {
            em.persist(solution);
        } else {
            em.merge(solution);
        }
        return solution.getSolutionId();
    }

    public Optional<Solution> findOne(Long solutionId) {
        return Optional.ofNullable(em.find(Solution.class, solutionId));
    }
}