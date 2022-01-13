package com.example.yangjie.structure.queue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Array<E> {

    private int size;
    private E[] data;


    //有参构造函数，传入数组的容量capacity构造Array
    public Array(int capacity) {
        size = 0;
        data = (E[]) new Object[capacity];
    }

    //无参构造函数，调用有参构造函数，默认定义capacity数组容量为10
    public Array() {
        this(10);
    }

    //获取数组中元素的个数
    public int getSize() {
        return size;
    }

    //获取数组的容量
    public int getCapacity() {
        return data.length;
    }

    //判断数组是否为空
    public boolean isEmpty() {
        return size == 0;
    }

    //在数组的尾部添加元素
    public void addLast(E e) {
        add(size, e);
    }

    //在数组的头部添加元素
    public void addFirst(E e) {
        add(0, e);
    }

    //在下标为index的位置插入一个元素e
    public void add(int index, E e) {
        if (index < 0 || index > size)
            throw new IllegalArgumentException("Add failed,index need >=0 and <=size");
        if (size == data.length) {
            resize(2 * data.length);
        }
        for (int i = size - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }
        data[index] = e;
        size++;
    }

    /*
     * 重新定义数组的容量，当数组元素增加或减少到一定条件时
     * 调用此方法更改数组容量，实现数组自动扩容与缩容
     */
    private void resize(int newCapacity) {
        E[] newData = (E[]) new Object[newCapacity];
        for (int i = 0; i < size; i++)
            newData[i] = data[i];
        data = newData;
    }

    //获取下标为index的元素
    public E get(int index) {
        if (index < 0 || index >= size)
            throw new IllegalArgumentException("Get failed,index is illegal");
        return data[index];
    }

    //获取第一个元素
    public E getFirst() {
        return get(0);
    }

    //移除元素e
    public void removeElement(E e) {
        int index = find(e);
        if (index != -1)
            remove(index);
    }

    //移除下标为index的元素，并返回被移除的元素
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if (index < 0 || index >= size)
            throw new IllegalArgumentException("Remove failed,index is illegal");
        if (size == data.length / 4 && data.length / 2 != 0) {
            resize(data.length / 2);
        }
        E temp = data[index];
        for (int i = index + 1; i < size; i++) {
            data[i - 1] = data[i];
        }
        size--;
        data[size] = null;
        return  temp;
    }

    //移除第一个元素并返回
    public E removeFirst() {
        return remove(0);
    }

    //移除最后一个元素并返回
    public E removeLast() {
        return remove(size - 1);
    }

    //判断数组中是否包含元素e
    public boolean contains(E e) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(e))
                return true;
        }
        return false;
    }

    //查询元素e的下标
    public int find(E e) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(e))
                return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(String.format("Array: size = %d , capacity = %d\n", size, data.length));
        res.append("[");
        for (int i = 0; i < size; i++) {
            res.append(data[i]);
            if (i != size - 1)
                res.append(", ");
        }
        res.append("]");
        return res.toString();
    }

    public static final ExecutorService testExecutorService = new ThreadPoolExecutor(1,
            1 ,
            1000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1),
            new ThreadFactoryBuilder().setNameFormat("grab-dsf-caller-%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) {
        List<Callable<Boolean>> taskList = new ArrayList<>(5);

        ThreadResultDTO threadResultDTO = new ThreadResultDTO();
        Callable<Boolean> tradeCaller = () -> {
            Thread.sleep(1000);
            threadResultDTO.setMember(true);

            return true;
        };
        Callable<Boolean> tradeCaller1 = () -> {
            Thread.sleep(2000);
            threadResultDTO.setProject("fff");
            return true;
        };
        Callable<Boolean> tradeCaller2 = () -> {
            Thread.sleep(1000);
            return true;
        };
        taskList.add(tradeCaller);
        taskList.add(tradeCaller1);
        taskList.add(tradeCaller2);
        try {
            testExecutorService.invokeAll(taskList,10,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println("1");
        }
        List<Callable<Boolean>> taskList2 = new ArrayList<>(5);
        System.out.println("taskList2");

        taskList2.add(tradeCaller2);
        try {
            testExecutorService.invokeAll(taskList2,10,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(threadResultDTO.member);
        System.out.println(threadResultDTO.project);

        System.out.println("=========结束");
    }

    /**
     * 线程结果集合
     */
    static class ThreadResultDTO{
        private String project ;
        private boolean member ;


        public String getProject() {
            return project;
        }

        public ThreadResultDTO setProject(String project) {
            this.project = project;
            return this;
        }

        public boolean isMember() {
            return member;
        }

        public ThreadResultDTO setMember(boolean member) {
            this.member = member;
            return this;
        }
    }

    public static class CancelFuturePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof RunnableFuture) {
                ((RunnableFuture)r).cancel(true);
                System.out.println(Thread.currentThread().getName() + ": cancel task ");
            }
        }
    }
}
