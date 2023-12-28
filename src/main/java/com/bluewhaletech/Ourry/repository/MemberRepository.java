package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Member;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    @PersistenceContext
    EntityManager em;

    public Long save(Member member) {
        if(member.getMemberId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
        return member.getMemberId();
    }
}