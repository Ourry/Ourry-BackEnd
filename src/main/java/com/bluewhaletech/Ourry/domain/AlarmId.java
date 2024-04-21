package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AlarmId implements Serializable {
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Column(name = "question_id", nullable = false)
    private Long questionId;
}