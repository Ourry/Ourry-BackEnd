package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.RefreshToken;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RedisRefreshTokenRepository extends Repository<RefreshToken, Long> {
//    Optional<RefreshToken> findByMemberId(Long memberId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void save(RefreshToken refreshToken);
}