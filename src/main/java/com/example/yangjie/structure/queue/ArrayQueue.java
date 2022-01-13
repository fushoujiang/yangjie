package com.example.yangjie.structure.queue;


public class ArrayQueue<E> implements Queue<E>{


    private E[] data;
    private int head;
    private int tail;
    private int count;


    public ArrayQueue(int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    public ArrayQueue() {
        this.data =  (E[]) new Object[10];;
    }

    @Override
    public void enqueue(E e) {
        if (count>=data.length){
            throw  new IllegalArgumentException("队列已满，无法入队");
        }
        data[tail] = e;
        count++;
        tail++;
    }



    @Override
    public E dequeue() {
        if (count<=0){
            throw  new IllegalArgumentException("队列为空，无法出队");
        }
        E e = data[head] ;
        data[head] = null;
        count--;
        head++;
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
        return count == 0;
    }


    public static void main(String[] args) {
        Queue<String > arrayQueue = new ArrayQueue();
        arrayQueue.enqueue("fsj");
        arrayQueue.enqueue("fsj1");
        arrayQueue.enqueue("fsj2");
        arrayQueue.enqueue("fsj3");
        arrayQueue.enqueue("fsj4");
        System.out.println(arrayQueue.toString());
        arrayQueue.dequeue();
        System.out.println(arrayQueue.toString());
        arrayQueue.dequeue();
        System.out.println(arrayQueue.toString());
        arrayQueue.dequeue();
        System.out.println(arrayQueue.toString());
        arrayQueue.dequeue();
        System.out.println(arrayQueue.toString());
        arrayQueue.dequeue();
        System.out.println(arrayQueue.toString());

        arrayQueue.enqueue("fsj");
        arrayQueue.enqueue("fsj1");
        arrayQueue.enqueue("fsj2");
        arrayQueue.enqueue("fsj3");
        arrayQueue.enqueue("fsj4");
        arrayQueue.enqueue("fsj5");



    }

    int sum =1;
    int tmp;

    private int add(int m, int n){

        if (m==0){
           return sum *n;
        }
        if (n==0){
            return sum *m;
        }
        if (m==n){
            sum = sum *m;
            m--;
            tmp ++;
            return sum;
        }else {
            sum = sum*(m+n);
            tmp ++;
        }
        return sum;
    }
}
