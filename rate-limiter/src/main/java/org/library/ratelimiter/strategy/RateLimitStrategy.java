package org.library.ratelimiter.strategy;

import java.time.Duration;

public interface RateLimitStrategy {
    boolean allowRequest(String key, int limit, Duration duration);
}
