package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Reply;
import com.bluewhaletech.Ourry.domain.Poll;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyJpaRepository extends org.springframework.data.repository.Repository<Reply, Long> {
    int countByPoll(Poll poll);

    Reply findByPoll(Poll poll);
}