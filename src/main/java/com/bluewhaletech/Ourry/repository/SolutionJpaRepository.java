package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Solution;
import com.bluewhaletech.Ourry.domain.Vote;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionJpaRepository extends org.springframework.data.repository.Repository<Solution, Long> {
    int countByVote(Vote vote);

    Solution findByVote(Vote vote);
}