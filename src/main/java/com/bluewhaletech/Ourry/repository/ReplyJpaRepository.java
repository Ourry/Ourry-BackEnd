package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Reply;
import com.bluewhaletech.Ourry.domain.Solution;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyJpaRepository extends org.springframework.data.repository.Repository<Reply, Long> {
    int countBySolution(Solution solution);

    Reply findBySolution(Solution solution);
}