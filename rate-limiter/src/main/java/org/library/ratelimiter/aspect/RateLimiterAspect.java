package org.library.ratelimiter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.library.ratelimiter.annotation.RateLimit;
import org.library.ratelimiter.exceptions.RateLimitExceededException;
import org.library.ratelimiter.service.ConfigService;
import org.library.ratelimiter.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimiterAspect {

    private final RateLimitService rateLimiterService;
    private final HttpServletRequest request;
   // private final Environment env;

    @Autowired
    private ConfigService configService;

    public RateLimiterAspect(RateLimitService rateLimiterService, HttpServletRequest request, Environment environment) {
        this.rateLimiterService = rateLimiterService;
        this.request = request;
       // this.env = environment;
    }

    @Around("@annotation(rateLimit)")
    public Object RateLimitCheck(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String strategy = rateLimit.strategy();
        String apiKey = rateLimit.api();
        // String keyType = rateLimit.keyType();
        String keyType = configService.getApiKeyType(apiKey);

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
                identifier = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";
                break;
        }


        String apiPath = request.getRequestURI();
        boolean allowed = rateLimiterService.isAllowed(strategy, identifier, apiPath, apiKey);
        if (!allowed) {
            throw new RateLimitExceededException("Too many requests - rate limit exceeded");
        }

        return joinPoint.proceed();
    }
}
