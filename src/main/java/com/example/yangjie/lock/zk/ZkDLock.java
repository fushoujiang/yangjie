package com.example.yangjie.lock.zk;

import com.example.yangjie.lock.DLock;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 基于zookeeper的分布式锁实现，使用apache curator的InterProcessMutex
 * @author wenqi.wu
 */
public class ZkDLock implements DLock {
    private static final Logger logger = LoggerFactory.getLogger(ZkDLock.class);
    private final String path;
    private InterProcessMutex interProcessMutex;
    private ZkDLockManager manager;

    public ZkDLock(CuratorFramework client, String path,ZkDLockManager manager) {
        Preconditions.checkNotNull(client);
        Preconditions.checkArgument(StringUtils.isNotBlank(path));
        this.path = path;
        this.interProcessMutex = new InterProcessMutex(client,path);
        this.manager = manager;
    }

    @Override
    public void lock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lockInterruptibly(-1,TimeUnit.MILLISECONDS);
    }

    @Override
    public void lockInterruptibly(long leaseTime, TimeUnit unit) throws InterruptedException {
        try {
            interProcessMutex.acquire();
        } catch (Exception e) {
            logger.error("the lockInterruptibly by source zk occurs error",e);
            Throwables.propagate(e);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(0L,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("the try lock get interrupted by source zk",e);
            return false;
        }
    }

    @Override
    public boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException {
        try {
            return interProcessMutex.acquire(waitTime,unit);
        } catch (Exception e) {
            logger.error("the try lock by source zk occurs error",e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        logger.warn("the try lock by source zk not support config the leaseTime of lock");
        return tryLock(waitTime,unit);
    }

    @Override
    public void unlock() {
        try {
            interProcessMutex.release();
        } catch (Exception e) {
            logger.error("the zk DLock unlock occurs error",e);
        }finally {
            manager.addCleanLockPath(path);
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
