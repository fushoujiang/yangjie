package com.example.yangjie.lock.factory;


import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;

public abstract class AbsLockFactory implements LockFactory{
    public static final ConcurrentMap<String, Lock> LOCK_MANAGER_CACHE = new ConcurrentHashMap<>();

    @Override
    public Lock getLock(String key) {
        if (!useCache()){
            return initLock(key);
        }
        Lock lock = LOCK_MANAGER_CACHE.get(key);
        if (Objects.isNull(lock)){
            lock = initLock(key);
            LOCK_MANAGER_CACHE.put(key,lock);
        }
        return lock;
    }

    public abstract Lock initLock(String key);

    public abstract boolean useCache();


}
