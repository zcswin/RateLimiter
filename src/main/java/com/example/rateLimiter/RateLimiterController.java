package com.example.rateLimiter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimiterController {

	@Autowired
	private RateLimiterService rateLimiterService;

	@GetMapping("/api1")
	public ResponseEntity<String> api1(HttpServletRequest request) {
		return handleRequest("api1", request);
	}

	@PostMapping("/api2")
	public ResponseEntity<String> api2(HttpServletRequest request) {
		return handleRequest("api2", request);
	}

	@PutMapping("/api3")
	public ResponseEntity<String> api3(HttpServletRequest request) {
		return handleRequest("api3", request);
	}

	private ResponseEntity<String> handleRequest(String apiName, HttpServletRequest request) {
		String userId = request.getParameter("userId");
		if (rateLimiterService.isAllowed(userId, apiName)) {
			return ResponseEntity.ok("Request successful");
		} else {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
		}
	}
}
