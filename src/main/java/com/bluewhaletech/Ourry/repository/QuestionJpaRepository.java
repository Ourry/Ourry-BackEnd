package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.ArticleCategory;
import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionJpaRepository extends org.springframework.data.repository.Repository<Question, Long> {
    @Query(value = "select q from Question q where (q.title like %:searchKeyword%) or (q.content like %:searchKeyword%) or (q.member.nickname like %:searchKeyword) order by q.createdAt desc")
    List<Question> searchQuestionList(String searchKeyword);
    List<Question> findByCategory(ArticleCategory category);

    boolean existsByMemberAndQuestionId(Member member, Long questionId);
}
