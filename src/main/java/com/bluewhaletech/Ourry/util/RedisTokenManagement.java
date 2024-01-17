package com.bluewhaletech.Ourry.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisTokenManagement {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisTokenManagement(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRefreshToken(String email, String token, Long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, token);
        redisTemplate.expire(email, Duration.ofSeconds(duration));
    }

    public String getRefreshToken(String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(email);
    }
}