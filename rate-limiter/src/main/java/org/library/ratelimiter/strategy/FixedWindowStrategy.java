package org.library.ratelimiter.strategy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("FIXED_WINDOW")
public class FixedWindowStrategy implements RateLimitStrategy {

    private final StringRedisTemplate redisTemplate;


    public FixedWindowStrategy(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @CircuitBreaker(name = "sampleService", fallbackMethod = "allowAllFallback")
    public boolean allowRequest(String keyBase, int limit, int burstLimit, Duration duration) {

        try {
            String redisKey = keyBase + ":fixedWindow";
            Long count = redisTemplate.opsForValue().increment(keyBase);

            if (count == 1) {
                redisTemplate.expire(keyBase, duration.toMillis(), TimeUnit.MILLISECONDS);
            }

            boolean allowed = count <= limit + burstLimit;
            log.debug("FixedWindow | Key: {} | Count: {} | Limit: {} | Allowed: {}", redisKey, count, limit, allowed);

            return allowed;
        } catch (Exception e) {
            throw new RuntimeException();
        }


    }

    @Override
    public boolean allowAllFallback(String keyBase, int limit, int burstLimit, Duration duration, Throwable t) {
        return true;
    }


}
