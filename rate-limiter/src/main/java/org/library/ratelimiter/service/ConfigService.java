package org.library.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class ConfigService {
    @Autowired
    private Environment environment;

    public int getApiLimit(String api) {
        return Integer.parseInt(environment.getProperty("ratelimiter.apis." + api + ".limit","3"));
    }

    public int getApiDuration(String api) {
        String durationStr = environment.getProperty("ratelimiter.apis." + api + ".duration","10");
        return Integer.parseInt(durationStr);
    }

    public String getApiKeyType(String api) {
        return environment.getProperty("ratelimiter.apis." + api + ".keyType","/testapi");
    }

    public String getStrategy(String api){
        return environment.getProperty("ratelimiter.apis." + api + ".strategy","FIXED_BUCKET");
    }

    public int getBurstLimit(String api){
        return Integer.parseInt(environment.getProperty("ratelimiter.apis." + api + ".burstlimit","0"));
    }
}
