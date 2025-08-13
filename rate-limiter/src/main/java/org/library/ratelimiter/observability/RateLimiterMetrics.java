package org.library.ratelimiter.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterMetrics{
    private final Counter totalRequests;
    private final Counter allowedRequests;
    private final Counter throttledRequests;

    public RateLimiterMetrics(MeterRegistry registry) {
        totalRequests = Counter.builder("ratelimiter.requests.total")
                .description("Total requests received")
                .register(registry);

        allowedRequests = Counter.builder("ratelimiter.requests.allowed")
                .description("Requests allowed")
                .register(registry);

        throttledRequests = Counter.builder("ratelimiter.requests.throttled")
                .description("Requests throttled")
                .register(registry);
    }

    public void incrementTotal() { totalRequests.increment(); }
    public void incrementAllowed() { allowedRequests.increment(); }
    public void incrementThrottled() { throttledRequests.increment(); }

}
