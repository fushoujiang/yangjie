package com.example.yangjie.limit.factory;


import com.example.yangjie.limit.entity.RateLimiterConfig;

public class LocalRateLimiterFactory extends AbsRateLimiterFactory {

    @Override
    public RateLimiterConfig reLoadLimiterConfig(RateLimiterConfig rateLimiterConfig) {
        return rateLimiterConfig;
    }
}
