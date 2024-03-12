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
    public Reply(String comment, Member member, Solution solution) {
        this.comment = comment;
        this.member = member;
        this.solution = solution;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reply_id", nullable = false)
    private Long replyId;

    @Column(name = "comment", nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "poll_poll_id", nullable = false)
    private Solution solution;
}