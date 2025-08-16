package org.library.ratelimiter.strategy;

import java.time.Duration;

public interface RateLimitStrategy {
    boolean allowRequest(String key, int limit,int burstLimit, Duration duration);
    boolean allowAllFallback(String key, int limit,int burstLimit, Duration duration, Throwable t);
}
