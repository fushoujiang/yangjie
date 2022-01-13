package com.example.yangjie.lock.interceptor;

import com.example.yangjie.lock.LocalLockAnnotation;
import com.example.yangjie.lock.LockFailException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbsLockInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbsLockInterceptor.class);

    public Object around(ProceedingJoinPoint joinPoint, Annotation annotation) throws Throwable{
        final LockEntity lockAnnotationEntity = lockAnnotation2LockConfig(joinPoint, annotation);
        final String lockKey = lockAnnotationEntity.getLockKey();
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        if (!lock( lockKey, lockAnnotationEntity.getTimeout())) {
            LOGGER.info("{}-{}, get lock fail:{}", joinPoint.getTarget().getClass().getName(), methodSignature.getName(), lockKey);
            final String failMethod = lockAnnotationEntity.getLockFailMethod();
            if (StringUtils.isBlank(failMethod)) {
                throw new LockFailException(joinPoint.getTarget().getClass().getName() + "--" + methodSignature.getName() + "...key=" + lockKey);
            }
            return invokeFallbackMethod(joinPoint, failMethod);
        }
        LOGGER.info("{}-{}, get lock success:{}", joinPoint.getTarget().getClass().getName(), methodSignature.getName(), lockKey);
        try {
            return joinPoint.proceed();
        }finally {
            unlock(lockKey);
            LOGGER.info("{}-{}, release lock:{}", joinPoint.getTarget().getClass().getName(), methodSignature.getName(), lockKey);
        }
    }

    /**
     * 注解和方法参数转换为LockEntity
     * @param joinPoint
     * @param annotation
     * @return
     */
    public abstract  LockEntity lockAnnotation2LockConfig(ProceedingJoinPoint joinPoint,Annotation annotation) ;


    public abstract  boolean lock(String lockKey, int timeout) ;

    public abstract  void unlock(String lockKey) ;




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
            throw new LockFailException(e);
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
            throw new LockFailException(e);
        }
    }

    /**
     * 加锁需要的数据类
     */
    public static class LockEntity{
        /**
         * 获取锁超时等待时间，单位为毫秒。
         *
         * <p>
         * 依赖具体锁实现是否支持timeout
         * </p>
         */
        private int timeout;
        /**
         * 锁的key
         */
        private String lockKey;
        /**
         * 失败之后执行本类的方法名,若无则抛异常
         * <p>
         *     入参和返回值和增强方法需保持一致
         * </p>
         */
        private String lockFailMethod;

        public int getTimeout() {
            return timeout;
        }

        public LockEntity setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public String getLockKey() {
            return lockKey;
        }

        public LockEntity setLockKey(String lockKey) {
            this.lockKey = lockKey;
            return this;
        }

        public String getLockFailMethod() {
            return lockFailMethod;
        }

        public LockEntity setLockFailMethod(String lockFailMethod) {
            this.lockFailMethod = lockFailMethod;
            return this;
        }
    }
}
