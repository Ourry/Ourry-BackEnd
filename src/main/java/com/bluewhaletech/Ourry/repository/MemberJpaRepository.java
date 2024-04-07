package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends org.springframework.data.repository.Repository<Member, String> {
    Member findByEmail(String email);

    @Query(value = "select m.fcmToken from Member m where m.memberId =: memberId")
    String findFcmTokenByMemberId(Long memberId);
}