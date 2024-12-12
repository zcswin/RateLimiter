package com.example.rateLimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RateLimiterControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		// 清空Redis中的测试数据
//		Set<String> keys = stringRedisTemplate.keys("user*"); 
//		if (keys != null) { stringRedisTemplate.delete(keys); } 
	}

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
		ResponseEntity<String> response = restTemplate.exchange("/api/api3?userId=" + userId, HttpMethod.PUT, null,
				String.class);
		assertEquals("Request successful", response.getBody());
	}

	@Test
	public void testApiRateLimiting() throws Exception {
		Set<String> keys = stringRedisTemplate.keys("user*");
		if (keys != null) {
			stringRedisTemplate.delete(keys);
		}
		for (int i = 0; i < 2000; i++) {
			String api;
			MockHttpServletRequestBuilder request;

			int apiNumber = (int) (Math.random() * 3) + 1;
			if (apiNumber == 1) {
				api = "/api/api1";
				request = MockMvcRequestBuilders.get(api);
			} else if (apiNumber == 2) {
				api = "/api/api2";
				request = MockMvcRequestBuilders.post(api);
			} else {
				api = "/api/api3";
				request = MockMvcRequestBuilders.put(api);
			}

			String userId = i < 1000 ? "user1" : "user2";
			mockMvc.perform(request.param("userId", userId))
					.andExpect(status().is(i < (userId.equals("user1") ? 1000 : 2000) ? HttpStatus.OK.value()
							: HttpStatus.TOO_MANY_REQUESTS.value()));
		}
	}

	@Test
	public void testApiRateLimitingUser1Api1() throws Exception {
		Set<String> keys = stringRedisTemplate.keys("user1:api1");
		if (keys != null) {
			stringRedisTemplate.delete(keys);
		}
		for (int i = 0; i < 2000; i++) {
			String api;
			MockHttpServletRequestBuilder request;
			api = "/api/api1";
			request = MockMvcRequestBuilders.get(api);
			String userId = "user1";
			int limit = 1000;
			mockMvc.perform(request.param("userId", userId))
					.andExpect(status().is(i < limit ? HttpStatus.OK.value() : HttpStatus.TOO_MANY_REQUESTS.value()));
		}
	}

	@Test
	public void testApiRateLimitingUser2Api2() throws Exception {
		Set<String> keys = stringRedisTemplate.keys("user2:api2");
		if (keys != null) {
			stringRedisTemplate.delete(keys);
		}
		for (int i = 0; i < 3000; i++) {
			String api;
			MockHttpServletRequestBuilder request;
			api = "/api/api2";
			request = MockMvcRequestBuilders.post(api);
			String userId = "user2";
			int limit = 2000;
			mockMvc.perform(request.param("userId", userId))
					.andExpect(status().is(i < limit ? HttpStatus.OK.value() : HttpStatus.TOO_MANY_REQUESTS.value()));
		}
	}

	@Test
	public void testApiRateLimitingUser3Api3() throws Exception {
		Set<String> keys = stringRedisTemplate.keys("user3:api3");
		if (keys != null) {
			stringRedisTemplate.delete(keys);
		}
		for (int i = 0; i < 2000; i++) {
			String api;
			MockHttpServletRequestBuilder request;
			api = "/api/api3";
			request = MockMvcRequestBuilders.put(api);
			String userId = "user3";
			int limit = 1000;
			mockMvc.perform(request.param("userId", userId))
					.andExpect(status().is(i < limit ? HttpStatus.OK.value() : HttpStatus.TOO_MANY_REQUESTS.value()));
		}
	}
}
