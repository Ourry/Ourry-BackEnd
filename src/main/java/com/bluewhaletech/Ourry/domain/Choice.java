package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 선택지 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice extends BaseEntity {
    @Builder
    public Choice(Long choiceId, String detail, Long sequence, Question question) {
        this.choiceId = choiceId;
        this.detail = detail;
        this.sequence = sequence;
        this.question = question;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "choice_id", nullable = false)
    private Long choiceId;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Column(name = "sequence", nullable = false)
    private Long sequence;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}