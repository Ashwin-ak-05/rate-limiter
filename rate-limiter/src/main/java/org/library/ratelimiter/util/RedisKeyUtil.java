package org.library.ratelimiter.util;

public class RedisKeyUtil {
    public static String buildKey(String keyType, String identifier, String apiPath) {
        return keyType.toLowerCase() + ":" + identifier + ":" + apiPath;
    }
}
