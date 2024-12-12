package com.example.rateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final Duration duration = Duration.ofMinutes(1);

    public boolean isAllowed(String userId, String apiName) {
        String key = generateKey(userId, apiName);
        String currentCountStr = stringRedisTemplate.opsForValue().get(key);
        Long currentCount = currentCountStr != null ? Long.parseLong(currentCountStr) : null;

        // 如果键不存在，则初始化计数并设置过期时间
        if (currentCount == null) {
            stringRedisTemplate.opsForValue().set(key, "1", duration);
            return true;
        } else if (currentCount < getRequestLimit(userId)) {
            stringRedisTemplate.opsForValue().increment(key);
            return true;
        }
        return false;
    }

    private int getRequestLimit(String userId) {
        if ("user1".equals(userId)) {
            return 10000; // 每分钟限制
        } else if ("user2".equals(userId)) {
            return 20000; // 每分钟限制
        } else {
            return 10000; // 默认每分钟限制
        }
    }

    private String generateKey(String userId, String apiName) {
        return userId + ":" + apiName;
    }
}
