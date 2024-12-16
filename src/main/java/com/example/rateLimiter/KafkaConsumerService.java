package com.example.rateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//@Service
public class KafkaConsumerService {

    @Autowired
    private RateLimiterService rateLimiterService;

    @KafkaListener(topics = "requests", groupId = "rate-limiter-group")
    public void consume(String message) {
        // 处理消息，例如解析用户ID和API名称
        // 并调用rateLimiterService.isAllowed方法
    }
}
