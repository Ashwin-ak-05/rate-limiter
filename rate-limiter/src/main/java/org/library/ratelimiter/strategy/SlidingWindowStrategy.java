package org.library.ratelimiter.strategy;

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
    public boolean allowRequest(String keyBase, int limit, Duration duration) {
        String redisKey = keyBase + ":slidingwindow";
        long now = System.currentTimeMillis();
        long windowStart = now - duration.toMillis();
        redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        Long count = redisTemplate.opsForZSet().zCard(redisKey);
        redisTemplate.expire(redisKey, duration.toMillis() * 2, TimeUnit.MILLISECONDS);
        boolean allowed = count <= limit;

        log.debug("SlidingWindow | Key: {} | Count: {} | Limit: {} | Allowed: {}", redisKey, count, limit, allowed);

        return allowed;
    }
}
