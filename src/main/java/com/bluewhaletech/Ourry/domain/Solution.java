package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Solution {
    @EmbeddedId
    private final PollId pollId = new PollId();

    @Builder
    public Solution(String opinion, Poll poll) {
        this.opinion = opinion;
        this.poll = poll;
    }

    @Column(name = "opinion")
    private String opinion;

    @MapsId("pollId")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Poll poll;
}