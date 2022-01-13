package com.example.yangjie.structure.queue;


public class LinkQueue<E> implements Queue<E>{

    private Node<E> head;
    private Node<E> tail;

    private int count;



    @Override
    public void enqueue(E e) {
        Node node =  new Node<>(null,e);
        if (head==null){
            head = node;
            tail = head;
        }else {
            tail.next = node;
            tail = node;
        }
        count++;
    }

    @Override
    public E dequeue() {
        if (head==null){
            throw  new IllegalArgumentException("队列为空，无法出队");
        }
        E e = head.data ;
        head = head.next;
        //出队之后若head为null则表示队列为空，需重置tail
        if (head == null ){
            tail = null;
        }
        count--;
        return e;
    }

    @Override
    public E peek() {
        return head.data;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    public static class Node<E>{
        Node<E> next;
        E data;

        public Node(Node<E> next, E data) {
            this.next = next;
            this.data = data;
        }
    }

    public static void main(String[] args) {
        Queue<String > arrayQueue = new LinkQueue<>();
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
    }
}
