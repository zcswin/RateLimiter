package com.example.rateLimiter;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class KafkaConfig {
	@Bean
	NewTopic rateLimiterTopic() {
		return new NewTopic("rate-limiter-topic", 1, (short) 1);
	}
}
