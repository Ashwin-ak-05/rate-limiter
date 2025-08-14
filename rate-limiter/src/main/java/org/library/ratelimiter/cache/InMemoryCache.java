package org.library.ratelimiter.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryCache {
//    public static class TokenInfo {
//        public double tokens;
//        public long lastRefreshed;
//
//        public TokenInfo(double tokens, long lastRefreshed) {
//            this.tokens = tokens;
//            this.lastRefreshed = lastRefreshed;
//        }
//    }

    // Key = strategyKey (e.g., userId + strategyName)
    private final ConcurrentHashMap<String, TokenInfo> cache = new ConcurrentHashMap<>();

    // Get TokenInfo for a key
    public TokenInfo get(String key) {
        return cache.get(key);
    }

    // Put/update TokenInfo for a key
    public void put(String key, double tokens, long lastRefreshed) {
        cache.put(key, new TokenInfo(tokens, lastRefreshed));
    }
}
