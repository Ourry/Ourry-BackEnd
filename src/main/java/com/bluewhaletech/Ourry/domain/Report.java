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
    protected Report(Long reportId, int reportCnt, ArticleType articleType, String content, Member member) {
        this.reportId = reportId;
        this.reportCnt = reportCnt;
        this.articleType = articleType;
        this.content = content;
        this.member = member;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "report_cnt", nullable = false)
    private int reportCnt;

    @Column(name = "article_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @Column(name = "member_id", nullable = false)
    private Member member;
}