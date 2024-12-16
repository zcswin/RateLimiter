package com.example.rateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	private final Duration duration = Duration.ofMinutes(1);
	@Autowired 
	private KafkaTemplate<String, String> kafkaTemplate;
	public boolean isAllowed(String userId, String apiName) {
		boolean isAllowed = false;
		String key = generateKey(userId, apiName);
		long startTime = System.nanoTime();
		String currentCountStr = stringRedisTemplate.opsForValue().get(key);
		Long currentCount = currentCountStr != null ? Long.parseLong(currentCountStr) : null;

		// 如果键不存在，则初始化计数并设置过期时间
		if (currentCount == null) {
			stringRedisTemplate.opsForValue().set(key, "1", duration);
			isAllowed = true;
		} else if (currentCount < getRequestLimit(userId)) {
			stringRedisTemplate.opsForValue().increment(key);
			isAllowed = true;
			// kafkaTemplate.send("rate-limiter-topic", key, currentCount.toString());
		}
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Time taken for rate limiting logic: " + duration + " nanoseconds");			
		
		return isAllowed;
	}

	public int getRequestLimit(String userId) {
		if ("user1".equals(userId)) {
			return 100; // 每分钟限制
		} else if ("user2".equals(userId)) {
			return 200; // 每分钟限制
		} else {
			return 500; // 默认每分钟限制
		}
	}

	private String generateKey(String userId, String apiName) {
		return userId + ":" + apiName;
	}
}
