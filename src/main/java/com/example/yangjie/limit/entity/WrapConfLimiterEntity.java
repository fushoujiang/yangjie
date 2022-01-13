package com.example.yangjie.limit.entity;


import com.google.common.util.concurrent.RateLimiter;

public class WrapConfLimiterEntity {

    private RateLimiterConfig rateLimiterConfig;
    private RateLimiter rateLimiter;

    public WrapConfLimiterEntity(RateLimiterConfig rateLimiterConfig, RateLimiter rateLimiter) {
        this.rateLimiterConfig = rateLimiterConfig;
        this.rateLimiter = rateLimiter;
    }

    public RateLimiterConfig getRateLimiterConfig() {
        return rateLimiterConfig;
    }

    public WrapConfLimiterEntity setRateLimiterConfig(RateLimiterConfig rateLimiterConfig) {
        this.rateLimiterConfig = rateLimiterConfig;
        return this;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public WrapConfLimiterEntity setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
        return this;
    }
}
