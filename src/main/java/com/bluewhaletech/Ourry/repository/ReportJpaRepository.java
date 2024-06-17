package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.ArticleType;
import com.bluewhaletech.Ourry.domain.Report;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportJpaRepository extends org.springframework.data.repository.Repository<Report, Long> {
    boolean existsByArticleIdAndArticleType(Long articleId, ArticleType articleType);

    Report findByArticleIdAndArticleType(Long articleId, ArticleType articleType);
}