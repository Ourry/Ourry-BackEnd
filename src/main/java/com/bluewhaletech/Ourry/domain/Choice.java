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
public class Choice {
    @Builder
    public Choice(Long choiceId, String detail, int sequence, Question question) {
        this.choiceId = choiceId;
        this.sequence = sequence;
        this.detail = detail;
        this.question = question;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "choice_id", nullable = false)
    private Long choiceId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "detail", nullable = false)
    private String detail;


    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}