package com.example.yangjie.concurrent;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    //数据结构

    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();
        reentrantLock.unlock();
    }
}
