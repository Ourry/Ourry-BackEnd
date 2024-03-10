package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PollId implements Serializable {
    @Column(name = "poll_id", nullable = false)
    private Long pollId;
}