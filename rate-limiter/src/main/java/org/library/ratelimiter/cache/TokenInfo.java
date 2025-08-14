package org.library.ratelimiter.cache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfo {
    public double tokens;
    public long lastRefreshed;

    public TokenInfo(double tokens, long lastRefreshed) {
        this.tokens = tokens;
        this.lastRefreshed = lastRefreshed;
    }
}
