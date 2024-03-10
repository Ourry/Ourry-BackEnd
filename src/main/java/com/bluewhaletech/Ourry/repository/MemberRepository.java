package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Member;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        if(member.getMemberId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
        return member.getMemberId();
    }

    public Member findOne(Long memberId) {
        return em.find(Member.class, memberId);
    }
}