package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_id", columnNames = "member_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Solution extends BaseEntity {
    @Builder
    public Solution(Long solutionId, String opinion, Member member, Choice choice) {
        this.solutionId = solutionId;
        this.opinion = opinion;
        this.member = member;
        this.choice = choice;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "solution_id", nullable = false)
    private Long solutionId;

    @Column(name = "opinion")
    private String opinion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;
}