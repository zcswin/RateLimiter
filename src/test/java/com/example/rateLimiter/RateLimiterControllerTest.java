package com.example.rateLimiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RateLimiterControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testApi1() {
        String userId = "user1";
        ResponseEntity<String> response = restTemplate.getForEntity("/api/api1?userId=" + userId, String.class);
        assertEquals("Request successful", response.getBody());
    }

    @Test
    public void testApi2() {
        String userId = "user1";
        ResponseEntity<String> response = restTemplate.postForEntity("/api/api2?userId=" + userId, null, String.class);
        assertEquals("Request successful", response.getBody());
    }

    @Test
    public void testApi3() {
        String userId = "user1";
        ResponseEntity<String> response = restTemplate.exchange("/api/api3?userId=" + userId, HttpMethod.PUT, null, String.class);
        assertEquals("Request successful", response.getBody());
    }
}
