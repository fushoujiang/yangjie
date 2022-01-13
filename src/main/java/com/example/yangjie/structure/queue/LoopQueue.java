package com.example.yangjie.structure.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LoopQueue<E> implements Queue<E>{

    private E[] data;

    private int head = -1 ;
    private int tail = -1 ;
    private int count;

    public LoopQueue( int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    public LoopQueue() {
        this(10);
    }

    @Override
    public void enqueue(E e) {
        if (isFull()){
            throw  new IllegalArgumentException("队列已满，无法入队");
        }
        if (head<0){
            head++;
        }
        data[tail = tail++ % (data.length)] = e;
        count++;
    }

    @Override
    public E dequeue() {
        E e = peek() ;
        data[head] = null;
        count--;
        head = head++ % data.length;
        if (count==0){
            tail = head=-1;
        }
        return e;
    }

    @Override
    public E peek() {
        return data[head];
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return tail == -1 && head==-1;
    }

    private boolean isFull(){
        int full = tail -head ;
        return full==-1 || full == data.length-1;
    }


    public static void main(String[] args) {
        LoopQueue<Integer> loopQueue = new LoopQueue<>(3);
        loopQueue.enqueue(0);
        loopQueue.enqueue(1);
        loopQueue.enqueue(2);
        loopQueue.dequeue();
        loopQueue.enqueue(3);
        loopQueue.dequeue();
        loopQueue.enqueue(4);
        loopQueue.dequeue();



    }
}
