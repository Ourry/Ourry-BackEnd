package com.bluewhaletech.Ourry.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseTimeEntity {
    @EmbeddedId
    private final AlarmId alarmId = new AlarmId();

    @Builder
    public Alarm(Member member, Question question, String alarmYN) {
        this.member = member;
        this.question = question;
        this.alarmYN = alarmYN;
    }

    @Setter
    @Column(name = "alarm_yn", nullable = false, length = 1)
    private String alarmYN;

    @MapsId("memberId")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Member member;

    @MapsId("questionId")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Question question;
}