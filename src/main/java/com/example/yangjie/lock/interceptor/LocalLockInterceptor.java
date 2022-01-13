package com.example.yangjie.lock.interceptor;

import com.example.yangjie.lock.LocalLockAnnotation;
import com.example.yangjie.lock.factory.LockFactory;
import com.google.common.base.Preconditions;
import org.apache.curator.shaded.com.google.common.hash.BloomFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Aspect
public class LocalLockInterceptor extends AbsLockInterceptor  {



    private LockFactory lockFactory;

    public LocalLockInterceptor(LockFactory lockFactory) {
        this.lockFactory = lockFactory;
    }

    @Around("@annotation(localLockAnnotation)")
    public Object lockAround(ProceedingJoinPoint joinPoint, LocalLockAnnotation localLockAnnotation) throws Throwable{
       return around(joinPoint, localLockAnnotation);
    }

    @Override
    public LockEntity lockAnnotation2LockConfig(ProceedingJoinPoint joinPoint,Annotation annotation) {
       LocalLockAnnotation localLockAnnotation  =  (LocalLockAnnotation)annotation;
        return new LockEntity()
                .setLockFailMethod(localLockAnnotation.failBackMethod().failBackMethod())
                .setLockKey(getLockKey(joinPoint,localLockAnnotation))
                .setTimeout(localLockAnnotation.timeout());
    }

    /**
     * 获取分布式锁
     *
     * @return 是否获取成功
     */
    @Override
    public boolean lock(String lockKey ,int timeout) {
        HashMap
        final Lock lock = lockFactory.getLock(lockKey);
        Preconditions.checkArgument(Objects.nonNull(lock), "解锁时获取的lock为null");
        try {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        final Lock lock = lockFactory.getLock(lockKey);
        Preconditions.checkArgument(Objects.nonNull(lock), "解锁时获取的lock为null");
        lock.unlock();
    }

    /**
     * 如果没有设置则使用方法签名
     * @param joinPoint
     * @return
     */
    public String getLockKey(ProceedingJoinPoint joinPoint,LocalLockAnnotation localLockAnnotation ) {
        if (null != localLockAnnotation.lockKey()) return localLockAnnotation.lockKey();
        return ((MethodSignature) joinPoint.getSignature()).getMethod().toGenericString();
    }


}
