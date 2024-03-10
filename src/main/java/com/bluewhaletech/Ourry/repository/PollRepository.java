package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.Poll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    public Poll findOne(Long voteId) {
        return em.find(Poll.class, voteId);
    }

    public Poll findByMemberAndChoice(Member member, Choice choice) {
        TypedQuery<Poll> query = em.createQuery("select p from Poll p where p.member =: member and p.choice =: choice", Poll.class)
                .setParameter("member", member)
                .setParameter("choice", choice);
        return query.getSingleResult();
    }
}