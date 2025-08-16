package org.library.ratelimiter.strategy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component("TOKEN_BUCKET")
public class TokenBucketStrategy implements RateLimitStrategy{

    private final  StringRedisTemplate redisTemplate;

    public TokenBucketStrategy(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }



    @Override
    //@CircuitBreaker(name = "testCircuitBreaker", fallbackMethod = "allowAllFallback")
    public boolean allowRequest(String keyBase, int limit, int burstLimit ,Duration duration) {
        long now = System.currentTimeMillis();
        String tokensKey = keyBase + ":tokens";
        String timestampKey = keyBase + ":timestamp";

        String tokenStr = redisTemplate.opsForValue().get(tokensKey);
        String timestampStr = redisTemplate.opsForValue().get(timestampKey);

        long lastRefreshed = timestampStr != null ? Long.parseLong(timestampStr) : now;
        double tokens = tokenStr != null ? Double.parseDouble(tokenStr) : limit;


        double ratePerMillis = (double) limit / duration.toMillis();
        double deltaMillis = Math.max(0, now - lastRefreshed);
        tokens = Math.min(limit, tokens + deltaMillis * ratePerMillis);

//        if (tokens < 1) {
//            return false;
//        }
//
//        tokens -= 1;
        boolean allowed = tokens >= 1;
        tokens = allowed ? tokens - 1 : tokens;


        redisTemplate.opsForValue().set(tokensKey, String.valueOf(tokens));
        redisTemplate.opsForValue().set(timestampKey, String.valueOf(now));

        redisTemplate.expire(tokensKey, duration.toMillis() * 2, java.util.concurrent.TimeUnit.MILLISECONDS);
        redisTemplate.expire(timestampKey, duration.toMillis() * 2, java.util.concurrent.TimeUnit.MILLISECONDS);

        log.debug("TokenBucket | Key: {} | Tokens: {} | Allowed: {}", keyBase, tokens, allowed);
        return allowed;



    }

    public boolean allowAllFallback(String key, int limit,int burstLimit, Duration duration, Throwable t){

        return true;
    }
}
