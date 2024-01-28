package com.bluewhaletech.Ourry.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {
    @Id
    private Long tokenId;

    @Indexed
    private String tokenValue;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshToken(Long tokenId, String tokenValue, Long expiration) {
        this.tokenId = tokenId;
        this.tokenValue = tokenValue;
        this.expiration = expiration;
    }
}