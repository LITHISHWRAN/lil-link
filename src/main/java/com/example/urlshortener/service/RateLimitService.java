package com.example.urlshortener.service;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    private final RedisTemplate<String, String> redis;

    public RateLimitService(
        @Qualifier("redisTemplate") RedisTemplate<String, String> redis
    ) {
        this.redis = redis;
    }

    public boolean isAllowed(String clientIp) {

        String key = "rate:" + clientIp;

        Long count = redis.opsForValue().increment(key);

        if (count != null && count == 1) {
            redis.expire(key, Duration.ofMinutes(1));
        }

        return count != null && count <= 20;
    }
}