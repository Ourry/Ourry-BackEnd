package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface JpaMemberRepository extends Repository<Member, Long> {
    Optional<Member> findByEmail(String email);
}