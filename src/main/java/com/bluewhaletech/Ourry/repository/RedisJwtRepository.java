package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.RefreshToken;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RedisJwtRepository extends Repository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long tokenId);

    boolean existsByTokenId(Long tokenId);

    void save(RefreshToken refreshToken);
}