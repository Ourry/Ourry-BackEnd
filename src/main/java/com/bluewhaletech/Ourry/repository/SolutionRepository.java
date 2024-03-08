package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.Solution;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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

    public Optional<Solution> findWithMemberAndChoice(Member member, Choice choice) {
        TypedQuery<Solution> query = em.createQuery("select s from Solution s where s.member =: member and s.choice =: choice", Solution.class)
                .setParameter("member", member)
                .setParameter("choice", choice);
        return query.getResultList().stream().findAny();
    }
}