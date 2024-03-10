package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity {
    @Builder
    public Reply(String comment, int seq, Member member, Vote vote) {
        this.comment = comment;
        this.seq = seq;
        this.member = member;
        this.vote = vote;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reply_id", nullable = false)
    private Long replyId;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "seq", nullable = false)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;
}