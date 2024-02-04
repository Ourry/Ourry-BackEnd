package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisJwtRepository extends org.springframework.data.repository.Repository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long tokenId);

    void save(RefreshToken refreshToken);

    void deleteById(Long tokenId);
}