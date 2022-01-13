package com.example.yangjie.lock.factory;

import java.util.concurrent.locks.Lock;

public interface LockFactory {
    Lock getLock(String key) ;
}
