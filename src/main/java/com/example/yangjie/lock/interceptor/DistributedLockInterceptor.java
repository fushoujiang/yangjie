package com.example.yangjie.lock.interceptor;

import com.example.yangjie.lock.DLock;
import com.example.yangjie.lock.DistributedLockAnnotation;
import com.example.yangjie.lock.LocalLockAnnotation;
import com.example.yangjie.lock.factory.LockFactory;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Aspect
public class DistributedLockInterceptor extends AbsLockInterceptor {


    private LockFactory lockFactory;

    public DistributedLockInterceptor(LockFactory lockFactory) {
        this.lockFactory = lockFactory;
    }

    @Around("@annotation(localLockAnnotation)")
    public Object lockAround(ProceedingJoinPoint joinPoint, LocalLockAnnotation localLockAnnotation) throws Throwable {
       return around(joinPoint, localLockAnnotation);
    }

    @Override
    public LockEntity lockAnnotation2LockConfig(ProceedingJoinPoint joinPoint,Annotation annotation) {
        DistributedLockAnnotation distributedLock = (DistributedLockAnnotation) annotation;
        return new LockEntity()
                .setLockFailMethod(distributedLock.failBackMethod().failBackMethod())
                .setLockKey(innerLockKey(joinPoint.getArgs(),distributedLock))
                .setTimeout(distributedLock.timeout());
    }

    /**
     * 获取分布式锁
     *
     * @return 是否获取成功
     */
    @Override
    public boolean lock(String lockKey ,int timeout) {
           Lock lock = lockFactory.getLock(lockKey);
        try {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        Lock lock = lockFactory.getLock(lockKey);
        Preconditions.checkArgument(Objects.nonNull(lock), "解锁时获取的lock为null");
        lock.unlock();
    }


    /**
     * 解析加锁内容
     *
     * @param args            要加锁方法入参
     * @param distributedLockAnnotation 加锁配置（注解）
     * @return 要加锁的key
     */
    private String innerLockKey(Object[] args, DistributedLockAnnotation distributedLockAnnotation) {
        StringBuilder lockKey = new StringBuilder(distributedLockAnnotation.lockPrefix());
        String[] keys = distributedLockAnnotation.keys();
        int index = 0;
        for (int keyIndex : distributedLockAnnotation.keyIndexes()) {
            Object arg = args[keyIndex];
            String key = keys[index++];
            if (isBasicType(key)) {
                lockKey.append("_").append(arg);
            } else {
                lockKey.append("_").append(parseKey(arg, key));
            }
        }
        return lockKey.toString();
    }
    /**
     * 判断是否基础数据类型
     *
     * @param name 要判断的占位符
     * @return true/false
     */
    private boolean isBasicType(String name) {
        return StringUtils.equalsIgnoreCase(name, "LONG") || StringUtils.equalsIgnoreCase(name, "INT")
                || StringUtils.equalsIgnoreCase(name, "STRING");
    }

    /**
     * 解析非基础数据类型的占位符
     *
     * @param obj 占位符对应参数
     * @param key 占位符内容
     * @return 解析后的内容
     */
    private String parseKey(Object obj, String key) {
        String[] stirs = key.substring(1, key.length()).split("\\.");
        Object currObj = obj;
        for (String fieldName : stirs) {
            try {
                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(obj.getClass(), fieldName);
                Method readMethod = propertyDescriptor.getReadMethod();
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                currObj = readMethod.invoke(obj);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return String.valueOf(currObj);
    }

}
