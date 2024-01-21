package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken")
@NoArgsConstructor
public class RefreshToken {
    @Builder
    public RefreshToken(Long memberId, String tokenValue, Long expiration) {
        this.memberId = memberId;
        this.tokenValue = tokenValue;
        this.expiration = expiration;
    }

    @Id @Indexed
    private Long memberId;

    @Indexed
    private String tokenValue;

    @TimeToLive
    private Long expiration;
}