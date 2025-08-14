package org.library.ratelimiter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.library.ratelimiter.annotation.RateLimit;
import org.library.ratelimiter.events.KafkaProducer;
import org.library.ratelimiter.exceptions.RateLimitExceededException;
import org.library.ratelimiter.observability.RateLimiterMetrics;
import org.library.ratelimiter.service.ConfigService;
import org.library.ratelimiter.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    private final RateLimitService rateLimiterService;
    private final HttpServletRequest request;
   // private final Environment env;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RateLimiterMetrics metrics;

    @Autowired
    private KafkaProducer kafkaProducer;

    public RateLimiterAspect(RateLimitService rateLimiterService, HttpServletRequest request, Environment environment) {
        this.rateLimiterService = rateLimiterService;
        this.request = request;
       // this.env = environment;
    }

    @Around("@within(rateLimit) || @annotation(rateLimit)")
    public Object RateLimitCheck(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        metrics.incrementTotal();


        if (rateLimit == null) {
            rateLimit = joinPoint.getTarget().getClass().getAnnotation(RateLimit.class);
        }

        String apiKey = rateLimit.api();
        // String keyType = rateLimit.keyType();
        String keyType = configService.getApiKeyType(apiKey);
        String strategy = configService.getStrategy(apiKey);

        String requestIP = request.getRemoteUser();
        String requestClient = request.getHeader("X-CLIENT-ID");
        String requestUser = request.getHeader("X-USER-ID");


        String identifier;

        switch (keyType.toUpperCase()) {
            case "IP":
                identifier = request.getRemoteUser();
                break;

            case "CLIENT":
                identifier = request.getHeader("X-CLIENT-ID");
                if (identifier == null) {
                    throw new IllegalArgumentException("Client-Id header missing");
                }
                break;

            case "USER":
            default:
                identifier = request.getHeader("X-USER-ID");
                break;
        }


        String apiPath = request.getRequestURI();
        boolean allowed = rateLimiterService.isAllowed(strategy, identifier, apiPath, apiKey);
        if (!allowed) {
            metrics.incrementThrottled();
            String logMessage = String.format("API=%s, User=%s, IP=%s, Strategy=%s, Timestamp=%d",
                    apiKey, requestUser, requestIP, strategy, System.currentTimeMillis());
            kafkaProducer.sendThrottleLog(logMessage);
            log.warn("Request throttled | API: {} | User: {} | IP: {} | Strategy: {} | Path: {}", apiKey, requestUser, requestIP, strategy, apiPath);
            throw new RateLimitExceededException("Too many requests - rate limit exceeded");
        }
        metrics.incrementAllowed();
        log.info("Request allowed | API: {} | User: {} | IP: {} | Strategy: {} | Path: {}", apiKey, requestUser, requestIP, strategy, apiPath);

        return joinPoint.proceed();
    }
}
