package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply {
    @Builder
    public Reply(String comment, Long seq) {
        this.comment = comment;
        this.seq = seq;
    }

    @EmbeddedId
    private final MemberSolutionId memberSolutionId = new MemberSolutionId();

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "seq", nullable = false)
    private Long seq;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("solutionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solution_id")
    private Solution solution;
}