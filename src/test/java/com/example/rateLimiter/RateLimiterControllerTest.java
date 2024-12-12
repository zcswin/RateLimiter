package com.example.rateLimiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
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
    
    @Autowired
	private MockMvc mockMvc;

	@Test
	public void testApiRateLimiting() throws Exception {
		for (int i = 0; i < 20000; i++) {
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

			String userId = i < 10000 ? "user1" : "user2";
			mockMvc.perform(request.param("userId", userId))
					.andExpect(status().is(i < (userId.equals("user1") ? 10000 : 20000) ? HttpStatus.OK.value()
							: HttpStatus.TOO_MANY_REQUESTS.value()));
		}
	}
}
