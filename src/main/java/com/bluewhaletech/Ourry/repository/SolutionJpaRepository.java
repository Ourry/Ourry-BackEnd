package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Solution;
import com.bluewhaletech.Ourry.domain.Poll;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionJpaRepository extends org.springframework.data.repository.Repository<Solution, Long> {
    boolean existsByPoll(Poll poll);

    Solution findByPoll(Poll poll);
}