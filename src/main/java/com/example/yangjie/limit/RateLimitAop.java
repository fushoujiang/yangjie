package com.example.yangjie.limit;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Aspect
@Component("RateLimitAop")
public class RateLimitAop {


    private static final ConcurrentMap<String, RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();


    @Around("@annotation(rateLimitAnnotation)")
    public Object lockAround(ProceedingJoinPoint point, RateLimitAnnotation rateLimitAnnotation)  {
        double qps = rateLimitAnnotation.perSecond();
        RateLimiter rateLimiter = RATE_LIMITER_CACHE.get(rateLimitAnnotation.group());
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(rateLimitAnnotation.perSecond());
            RATE_LIMITER_CACHE.put(rateLimitAnnotation.group(), rateLimiter);
        }
        System.out.println(rateLimiter.getRate());
        if (rateLimitAnnotation.isWait()) {
            //阻塞获取令牌
            rateLimiter.acquire();
        } else {
            String methodName = point.getSignature().getName();
            //非阻塞获取令牌
            if (!rateLimiter.tryAcquire(rateLimitAnnotation.timeOut(), rateLimitAnnotation.timeOutUnit())) {
                if (!Strings.isNullOrEmpty(rateLimitAnnotation.failBackMethod())) {
                    return invokeFallbackMethod(point, rateLimitAnnotation.failBackMethod());
                }else {
                    System.out.println("【方法】 "+ methodName +" 调用次数超过" + rateLimitAnnotation.perSecond() + "被限流");
                    return null;
                }
            }
        }
        try {
            return point.proceed();
        } catch (Throwable throwable) {
            throw  new RateLimitException(throwable);
        }
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
    }

}
