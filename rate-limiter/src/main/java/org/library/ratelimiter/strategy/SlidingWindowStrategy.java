package org.library.ratelimiter.strategy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("SLIDING_WINDOW")
public class SlidingWindowStrategy implements RateLimitStrategy {

    private final StringRedisTemplate redisTemplate;

    public SlidingWindowStrategy(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    //@CircuitBreaker(name = "testCircuitBreaker", fallbackMethod = "allowAllFallback")
    public boolean allowRequest(String keyBase, int limit,int burstLimit , Duration duration) {
        String redisKey = keyBase + ":slidingwindow";
        long now = System.currentTimeMillis();
        long windowStart = now - duration.toMillis();
        redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        Long count = redisTemplate.opsForZSet().zCard(redisKey);
        redisTemplate.expire(redisKey, duration.toMillis() * 2, TimeUnit.MILLISECONDS);
        boolean allowed = count <= limit + burstLimit ;

        log.debug("SlidingWindow | Key: {} | Count: {} | Limit: {} | Allowed: {}", redisKey, count, limit, allowed);

        return allowed;
    }

    @Override
    public boolean allowAllFallback(String strategyName, String identifier, String apiPath, String apiKey, Throwable t) {
        return true;
    }
}
