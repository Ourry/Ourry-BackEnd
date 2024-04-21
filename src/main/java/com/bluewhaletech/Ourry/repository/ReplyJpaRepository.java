package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Reply;
import com.bluewhaletech.Ourry.domain.Solution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyJpaRepository extends org.springframework.data.repository.Repository<Reply, Long> {
    int countBySolution(Solution solution);

    List<Reply> findBySolution(Solution solution);

    @Query("select r from Reply r where r.solution = :solution and r <> :reply")
    List<Reply> findBySolutionExceptReplier(@Param("solution") Solution solution, @Param("reply") Reply reply);
}