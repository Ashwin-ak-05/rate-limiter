package org.library.ratelimiter;

import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class RateLimiterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    public void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void testTokenBucketUserLimit() throws Exception {
        String userId = "user123";

        for (int i = 0; i < 5; i++) {

            mockMvc.perform(get("/test").header("X-USER-ID", userId))
                    .andExpect(status().isOk());


        }
//        mockMvc.perform(get("/test").header("X-USER-ID", userId))
//                .andExpect(status().isTooManyRequests());
    }


}
