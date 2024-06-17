package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportDetail {
    @Builder
    public ReportDetail(Long id, ReportCategory category, String reason, Member author, Member target, Report report) {
        this.id = id;
        this.category = category;
        this.reason = reason;
        this.author = author;
        this.target = target;
        this.report = report;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "target_id", nullable = false)
    private Member target;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}