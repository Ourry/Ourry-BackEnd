package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Alarm;
import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmJpaRepository extends org.springframework.data.repository.Repository<Alarm, Long> {
    boolean existsAlarmByMemberAndQuestion(Member member, Question question);

    Alarm findByMemberAndQuestion(Member member, Question question);

    @Query(value = "select a.alarmYN from Alarm a where a.member = :member and a.question = :question")
    char findAlarmYNByMemberAndQuestion(@Param("member") Member member, @Param("question") Question question);
}