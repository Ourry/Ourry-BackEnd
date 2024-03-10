package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Question;
import com.bluewhaletech.Ourry.domain.Vote;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteJpaRepository extends org.springframework.data.repository.Repository<Vote, Long> {
    List<Vote> findByQuestion(Question question);

    Vote findByChoice(Choice choice);
}