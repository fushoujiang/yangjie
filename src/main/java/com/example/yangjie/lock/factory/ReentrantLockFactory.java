package com.example.yangjie.lock.factory;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockFactory extends AbsLockFactory {

    @Override
    public Lock initLock(String key ) {
        return new ReentrantLock();
    }

    @Override
    public boolean useCache() {
        return true;
    }
}
