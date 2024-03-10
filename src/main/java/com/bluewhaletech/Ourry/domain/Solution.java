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
    private final VoteId voteId = new VoteId();

    @Builder
    public Solution(String opinion, Vote vote) {
        this.opinion = opinion;
        this.vote = vote;
    }

    @Column(name = "opinion")
    private String opinion;

    @MapsId("voteId")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Vote vote;
}