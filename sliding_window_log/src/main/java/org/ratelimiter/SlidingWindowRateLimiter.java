package org.ratelimiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.UUID;

public class SlidingWindowRateLimiter {
   private final Jedis jedis;
   private final int limit;
   private final long windowSize;

   public SlidingWindowRateLimiter(Jedis jedis, int limit, int windowSize){
       this.jedis = jedis;
       this.limit = limit;
       this.windowSize = windowSize;
   }

   public boolean isAllowed(String clientId){
       String key = "rate_limit" + clientId;
       String fieldKey = UUID.randomUUID().toString();

       long requestCount = jedis.hlen(key);
       boolean isAllowed = requestCount < limit;

       if(isAllowed){
           Transaction transaction = jedis.multi();
           transaction.hset(key,fieldKey,"");
           transaction.hexpire(key,windowSize,fieldKey);
           transaction.exec();
       }

       return isAllowed;
   }

}
