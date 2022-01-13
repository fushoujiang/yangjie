package com.example.yangjie.limit;

import com.example.yangjie.limit.entity.RateLimiterConfig;
import com.example.yangjie.limit.entity.WrapConfLimiterEntity;
import com.example.yangjie.limit.factory.LocalRateLimiterFactory;
import com.example.yangjie.limit.factory.RateLimiterFactory;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 注意一个项目只能初始化一个
 * 如果初始化多个则会导致交集
 */
@Aspect
@Component("rateLimitInterceptor")
public class RateLimitInterceptor {

    RateLimiterFactory rateLimiterFactory = new LocalRateLimiterFactory();

//    public RateLimitInterceptor(RateLimiterFactory rateLimiterFactory) {
//        this.rateLimiterFactory = rateLimiterFactory;
//    }

    @Around("@annotation(rateLimitAnnotation)")
    public Object around(ProceedingJoinPoint point, RateLimitAnnotation rateLimitAnnotation) throws Throwable {
        final WrapConfLimiterEntity rateLimitConf = rateLimiterFactory.getWrapConfLimiter(RateLimiterConfig.rateLimitAnnotation2RateLimiterConfDTO(rateLimitAnnotation));
        final RateLimiter rateLimiter = rateLimitConf.getRateLimiter();
        final RateLimiterConfig rateLimiterConfig = rateLimitConf.getRateLimiterConfig();
        if (rateLimiterConfig.isWait()) {
            //阻塞获取令牌
            rateLimiter.acquire();
            return point.proceed();
        }
        //非阻塞获取令牌
        if (rateLimiter.tryAcquire(rateLimiterConfig.getTimeOut(), rateLimiterConfig.getTimeOutUnit())) {
            return point.proceed();
        }
        if (StringUtils.isNotBlank(rateLimiterConfig.getFailBackMethod())) {
            return invokeFallbackMethod(point, rateLimiterConfig.getFailBackMethod());
        }
        throw new RateLimitException("【方法】" + point.getSignature().getName() + "调用次数超过" + rateLimiterConfig.getPerSecond() + "被限流");
    }


    private Method findFallbackMethod(ProceedingJoinPoint joinPoint, String fallbackMethodName) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Method fallbackMethod = null;
        try {
            //这里通过判断必须取和原方法一样参数的fallback方法
            fallbackMethod = joinPoint.getTarget().getClass().getMethod(fallbackMethodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RateLimitException(e);
        }
        return fallbackMethod;
    }


    private Object invokeFallbackMethod(ProceedingJoinPoint joinPoint, String fallback) {
        Method method = findFallbackMethod(joinPoint, fallback);
        method.setAccessible(true);
        try {
            Object invoke = method.invoke(joinPoint.getTarget(), joinPoint.getArgs());
            return invoke;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RateLimitException(e);
        }
    }
}
