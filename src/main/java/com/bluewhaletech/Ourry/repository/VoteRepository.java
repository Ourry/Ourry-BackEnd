package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.Vote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VoteRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Vote vote) {
        if(vote.getVoteId() == null) {
            em.persist(vote);
        } else {
            em.merge(vote);
        }
        return vote.getVoteId();
    }

    public Vote findOne(Long voteId) {
        return em.find(Vote.class, voteId);
    }

    public Optional<Vote> findByMemberAndChoice(Member member, Choice choice) {
        TypedQuery<Vote> query = em.createQuery("select v from Vote v where v.member =: member and v.choice =: choice", Vote.class)
                .setParameter("member", member)
                .setParameter("choice", choice);
        return query.getResultList().stream().findAny();
    }
}