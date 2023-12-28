package com.bluewhaletech.Ourry.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String checkAuthentication(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, "auth");
    }

    public String getAuthenticationCode(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, "code");
    }

    public void setAuthenticationExpire(String email, String code, long duration) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(email, "code", code);
        hashOperations.put(email, "auth", "N");
        redisTemplate.expire(email, Duration.ofMinutes(duration));
    }

    public void setAuthenticationComplete(String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(email, "auth", "Y");
    }
}