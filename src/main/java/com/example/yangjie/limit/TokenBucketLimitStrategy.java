package com.example.yangjie.limit;

import com.example.yangjie.jmeter.LimitStrategy;
import com.google.common.util.concurrent.RateLimiter;

public class TokenBucketLimitStrategy implements LimitStrategy {
    private static RateLimiter limiter = RateLimiter.create(5);
    @Override
    public boolean limit() {
        limiter.tryAcquire(1);
        return true;
    }
}
