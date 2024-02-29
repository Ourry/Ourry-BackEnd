package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class MemberSolutionId implements Serializable {
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "solution_id", nullable = false)
    private Long solutionId;
}