package com.example.yangjie.limit.factory;


import com.example.yangjie.limit.entity.RateLimiterConfig;
import com.example.yangjie.limit.entity.WrapConfLimiterEntity;

public interface RateLimiterFactory {

    WrapConfLimiterEntity getWrapConfLimiter(final RateLimiterConfig rateLimiterConfig);

}
