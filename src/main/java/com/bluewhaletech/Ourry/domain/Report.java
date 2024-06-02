package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    @Builder
    public Report(Long id, ReportStatus status, ArticleType articleType, Long articleId) {
        this.id = id;
        this.status = status;
        this.articleType = articleType;
        this.articleId = articleId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "report_id", nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "article_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    @Column(name = "article_id", nullable = false)
    private Long articleId;
}