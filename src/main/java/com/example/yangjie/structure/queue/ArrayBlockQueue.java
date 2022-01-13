package com.example.yangjie.structure.queue;

import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockQueue<E> implements Queue<E>{

    private E[] data;
    private int head;
    private int tail;
    private int size;



    @Override
    public void enqueue(E e) {

    }

    @Override
    public E dequeue() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
