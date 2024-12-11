package com.example.rateLimiter;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.Duration;

@SpringBootTest
public class RateLimiterServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private RateLimiterService rateLimiterService;

    public RateLimiterServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsAllowed() {
        String userId = "user1";
        String api = "api1";
        int limit = 10000;

        when(redisTemplate.opsForValue().increment(anyString(), eq(1L))).thenReturn(1L);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);

        assertTrue(rateLimiterService.isAllowed(userId, api, limit));

        when(redisTemplate.opsForValue().increment(anyString(), eq(1L))).thenReturn(10001L);

        assertFalse(rateLimiterService.isAllowed(userId, api, limit));
    }
}
