package com.bluewhaletech.Ourry.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisBlackListManagement {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisBlackListManagement(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String checkLogout(String accessToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(accessToken);
    }

    public void setAccessTokenExpire(String accessToken, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(accessToken, "LOGOUT");
        redisTemplate.expire(accessToken, Duration.ofMillis(duration));
    }
}