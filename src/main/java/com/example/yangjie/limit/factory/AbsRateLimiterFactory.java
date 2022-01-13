package com.example.yangjie.limit.factory;

import com.example.yangjie.limit.entity.RateLimiterConfig;
import com.example.yangjie.limit.entity.WrapConfLimiterEntity;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public  abstract class AbsRateLimiterFactory implements RateLimiterFactory {
    public static final ConcurrentMap<String, WrapConfLimiterEntity> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    @Override
    public WrapConfLimiterEntity getWrapConfLimiter( final RateLimiterConfig rateLimiterConfig) {
        checkArguments(rateLimiterConfig);
        final RateLimiterConfig reLoadLimiterConfig = reLoadLimiterConfig(rateLimiterConfig);
        checkRemoteLimiterConfig(rateLimiterConfig,reLoadLimiterConfig);

        final String cacheKey = buildCacheKey(reLoadLimiterConfig);
        WrapConfLimiterEntity wrapConfLimiterEntity = getCache(cacheKey);
        if (needUpdateCache(wrapConfLimiterEntity, reLoadLimiterConfig)) {
            RateLimiter rateLimiter = RateLimiter.create(reLoadLimiterConfig.getPerSecond());
            putCache(cacheKey, wrapConfLimiterEntity = new WrapConfLimiterEntity(reLoadLimiterConfig, rateLimiter));
        }
        return wrapConfLimiterEntity;
    }

    /**
     *
     * @param rateLimiterConfig
     * @return
     */
    public abstract RateLimiterConfig reLoadLimiterConfig(final RateLimiterConfig rateLimiterConfig);



    private WrapConfLimiterEntity getCache(final String key) {
        return RATE_LIMITER_CACHE.get(key);
    }

    private WrapConfLimiterEntity putCache(final String key, final WrapConfLimiterEntity wrapConfLimiter) {
        return RATE_LIMITER_CACHE.put(key, wrapConfLimiter);
    }

    /**
     * 从缓存中获取
     * @param wrapConfLimiter
     * @param rateLimiterConfig
     * @return
     */
    private boolean needUpdateCache(WrapConfLimiterEntity wrapConfLimiter, RateLimiterConfig rateLimiterConfig) {
        if (wrapConfLimiter == null) return true;
        if (!wrapConfLimiter.getRateLimiterConfig().equals(rateLimiterConfig)) return true;
        if (wrapConfLimiter.getRateLimiter().getRate() != rateLimiterConfig.getPerSecond()) return true;
        return false;
    }

    private String buildCacheKey(RateLimiterConfig rateLimiterConfig) {
        return rateLimiterConfig.getProject() + "_" + rateLimiterConfig.getGroup();
    }


    private void checkRemoteLimiterConfig(RateLimiterConfig local,RateLimiterConfig remote){
        checkArguments(local);
        checkArguments(remote);
        Preconditions.checkArgument(local.getGroup().equals(remote.getGroup()), "远程加载限流器配置分组group和本地不一致");
        Preconditions.checkArgument(local.getProject().equals(remote.getProject()), "远程加载限流器配置分组project和本地不一致");
    }
    private void checkArguments(RateLimiterConfig rateLimiterConfig) {
        Preconditions.checkArgument(StringUtils.isNotBlank(rateLimiterConfig.getGroup()), "限流器分组group不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(rateLimiterConfig.getProject()), "限流器项目project不能为空");
        Preconditions.checkArgument(rateLimiterConfig.getPerSecond() > 0.0D, "限流器perSecond>0");
    }


}
