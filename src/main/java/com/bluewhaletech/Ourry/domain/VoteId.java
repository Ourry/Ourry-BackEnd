package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class VoteId implements Serializable {
    @Column(name = "vote_id", nullable = false)
    private Long voteId;
}