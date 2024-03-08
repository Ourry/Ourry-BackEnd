package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Choice;
import com.bluewhaletech.Ourry.domain.Solution;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionJpaRepository extends org.springframework.data.repository.Repository<Solution, Long> {
    Solution findByChoice(Choice choice);

}