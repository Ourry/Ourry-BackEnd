package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends org.springframework.data.repository.Repository<Member, String> {
    Optional<Member> findByEmail(String email);
}