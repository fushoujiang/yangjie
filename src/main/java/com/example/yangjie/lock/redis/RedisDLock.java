package com.example.yangjie.lock.redis;

import com.example.yangjie.id.idworker.IdGenerator;
import com.example.yangjie.lock.DLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

/**
 * 基于Redis的分布式锁实现
 * @author wenqi.wu
 */
public class RedisDLock implements DLock {
    private static final Logger logger = LoggerFactory.getLogger(RedisDLock.class);
    private static final long LOCK_LEASE_TIME_MILLIS = 30000;//默认锁持有时间为30秒
    private static final long PARK_WAIT_TIME_MILLIS = 1000L;//线程park的时间
    private static final ThreadLocal<LockData> threadLocalData = new ThreadLocal<LockData>();
    private final CommandSync sync;
    private final IdGenerator<String> idGenerator;
    private final String name;


    public RedisDLock(CommandSync sync, String name,IdGenerator<String> idGenerator) {
        this.sync = sync;
        this.name = name;
        this.idGenerator = idGenerator;
    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(0L,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("the try lock get interrupted by source redis",e);
            return false;
        }
    }

    @Override
    public boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException{
        return tryLock(waitTime,-1L,unit);
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException{
        LockData lockData = threadLocalData.get();// Reentrant
        if(lockData != null){
            lockData.lockCount.incrementAndGet();
            return true;
        }
        long current = System.currentTimeMillis();
        if(unit == null){
            unit = TimeUnit.MILLISECONDS;
        }
        long newLeaseTime = LOCK_LEASE_TIME_MILLIS;
        if(leaseTime > 0){
            newLeaseTime = unit.toMillis(leaseTime);
        }
        if(innerLock(newLeaseTime)){
            logger.info("try lock success");
            return true;
        }
        long millisToWait = unit.toMillis(waitTime) - (System.currentTimeMillis() - current);
        if(millisToWait <= 0){
            logger.info("try lock exceed the wait time out");
            return false;
        }
        current = System.currentTimeMillis();
        for(;;){
            if(innerLock(newLeaseTime)){
                return true;
            }
            if(System.currentTimeMillis() - current - PARK_WAIT_TIME_MILLIS > millisToWait){
                break;
            }
            LockSupport.parkNanos(this,TimeUnit.MILLISECONDS.toNanos(PARK_WAIT_TIME_MILLIS));
            if (Thread.interrupted())
                throw new InterruptedException();
        }
        return false;
    }

    /**
     * 内部加锁
     * @param millisToLeaseTime 锁持有时间
     * @return  boolean
     */
    private boolean innerLock(long millisToLeaseTime){
        String randValue = idGenerator.generateNextId();
        boolean acquire = sync.tryAcquire(name,randValue,millisToLeaseTime);
        if(acquire){
            LockData newLockData = new LockData(randValue);
            threadLocalData.set(newLockData);
            return true;
        }
        return false;
    }

    @Override
    public void lock() {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lockInterruptibly(-1L,TimeUnit.SECONDS);
    }

    @Override
    public void lockInterruptibly(long leaseTime, TimeUnit unit) throws InterruptedException {
        LockData lockData = threadLocalData.get();
        if(lockData != null){
            lockData.lockCount.incrementAndGet();
            return ;
        }
        long newLeaseTime = LOCK_LEASE_TIME_MILLIS;
        if(leaseTime > 0 && unit != null){
            newLeaseTime = unit.toMillis(leaseTime);
        }
        if(innerLock(newLeaseTime)){
            logger.info("try lock success");
            return;
        }
        for(;;){
            if(innerLock(newLeaseTime)){
                return ;
            }
            LockSupport.parkNanos(this,TimeUnit.MILLISECONDS.toNanos(PARK_WAIT_TIME_MILLIS));
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    }

    @Override
    public void unlock() {
        LockData lockData = threadLocalData.get();
        long threadId = Thread.currentThread().getId();
        if(lockData == null){
            logger.info("the current thread [{}] cannot hold the lock",threadId);
            return;
        }
        int newLockCount = lockData.lockCount.decrementAndGet();
        if(newLockCount > 0 ){
            logger.info("lock has reentrant,dela");
            return;
        }
        if(newLockCount < 0){
            logger.info("Lock count has gone negative for lock:{}",name);
            return;
        }
        try {
            sync.tryRelease(name,lockData.lockRandValue);
        } finally {
            threadLocalData.remove();
        }
    }

    @Override
    public Condition newCondition() {
        throw  new UnsupportedOperationException();
    }

    /**
     * 内部类保存锁信息
     */
    private static class LockData{
        final AtomicInteger lockCount = new AtomicInteger(1);
        String lockRandValue;
        //constructor
        private LockData(String lockRandValue){
            this.lockRandValue = lockRandValue;
        }
    }
}
