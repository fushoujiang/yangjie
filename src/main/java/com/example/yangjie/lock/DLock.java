package com.example.yangjie.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Distributed Lock Manager(DLM) 接口定义
 * @author wenqi.wu
 */
public interface DLock extends Lock {


    /**
     * 获取锁可响应中断
     * @param leaseTime 获取锁后最大持有的锁有效期
     * @param unit  锁时间单位
     * @throws InterruptedException
     */
    void lockInterruptibly(long leaseTime, TimeUnit unit) throws InterruptedException;



    /**
     * 尝试获取锁，并尽快返回获取的结果
     * @param waitTime  获取锁的最大等待时间
     * @param leaseTime 获取锁后最大持有的锁有效期
     * @param unit  锁时间单位
     * @return  boolean
     * @throws InterruptedException
     */
    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;
}
