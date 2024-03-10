package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Question;
import com.bluewhaletech.Ourry.domain.Poll;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollJpaRepository extends org.springframework.data.repository.Repository<Poll, Long> {
    List<Poll> findByQuestion(Question question);
}