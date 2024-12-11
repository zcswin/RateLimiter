package com.example.rateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimiterController {

    @Autowired
    private RateLimiterService rateLimiterService;

    private static final int LIMIT = 10000;

    @GetMapping("/api1")
    public String api1(@RequestParam String userId) {
        return handleRequest(userId, "api1");
    }

    @PostMapping("/api2")
    public String api2(@RequestParam String userId) {
        return handleRequest(userId, "api2");
    }

    @PutMapping("/api3")
    public String api3(@RequestParam String userId) {
        return handleRequest(userId, "api3");
    }

    private String handleRequest(String userId, String api) {
        if (rateLimiterService.isAllowed(userId, api, LIMIT)) {
            return "Request successful";
        } else {
            return "Rate limit exceeded";
        }
    }
}
