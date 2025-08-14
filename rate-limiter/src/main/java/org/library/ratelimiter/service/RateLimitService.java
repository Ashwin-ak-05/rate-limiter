package org.library.ratelimiter.service;

import org.library.ratelimiter.strategy.RateLimitStrategy;
import org.library.ratelimiter.strategy.TokenBucketStrategy;
import org.library.ratelimiter.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class RateLimitService {
    private final Map<String, RateLimitStrategy> strategies;

    // Map keyType to limit values (hardcoded or configurable)
    private static final Map<String, Integer> LIMITS = Map.of(
            "USER", 3,
            "IP", 50,
            "CLIENT", 5
    );

    private static Duration DURATION = Duration.ofSeconds(10);
  //  private final Environment env;

    @Autowired
    private ConfigService configService;

    @Autowired
    public RateLimitService(Map<String, RateLimitStrategy> strategies, Environment environment){
        this.strategies = strategies;
       // this.strategies.put("TOKEN_BUCKET", )
       // this.env = environment;
    }

    public boolean isAllowed(String strategyName, String identifier, String apiPath, String apiKey){
        String keyType = configService.getApiKeyType(apiKey);
        int duration_in_sec = configService.getApiDuration(apiKey);
        DURATION = Duration.ofSeconds(duration_in_sec);
        RateLimitStrategy strategy = this.strategies.get(strategyName);
        int limit = configService.getApiLimit(apiKey);
        String redisKey = RedisKeyUtil.buildKey(keyType,identifier,apiPath);
        return strategy.allowRequest(redisKey,limit,DURATION);

    }
}
