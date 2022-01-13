package com.example.yangjie.structure.queue;

import java.util.Collection;
import java.util.NoSuchElementException;

public interface Queue<E>{
    void enqueue(E e);
    E dequeue();
    E peek();
    int getCount();
    boolean isEmpty();
}
