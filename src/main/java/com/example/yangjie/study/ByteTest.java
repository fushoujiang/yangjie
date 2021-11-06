package com.example.yangjie.study;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Byte使用场景
 */
public class ByteTest {
    public int sum(int i,int y){
        return i+y;
    }

    public static final ExecutorService dsfExecutor = new ThreadPoolExecutor(1,
            1 ,
            1000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy());
    static class MyRejectedExecutionHandler implements RejectedExecutionHandler{
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if(r instanceof Future){
                ((Future<?>) r).cancel(true);
                System.out.println("任务取消");
            }
        }
    }
    public static void main(String[] args) {
        Callable<Boolean> tradeCaller = () -> {
            Thread.sleep(1000L);
            return true;
        };
        Callable<Boolean> tradeCaller1 = () -> {
            Thread.sleep(1000L);
            return true;
        };
        Callable<Boolean> tradeCaller2 = () -> {
            Thread.sleep(1000L);
            return true;
        };
        List<Callable<Boolean>> list = new ArrayList<>();
        list.add(tradeCaller);
        list.add(tradeCaller1);
        list.add(tradeCaller2);
        try {
            dsfExecutor.invokeAll(list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("任务结束");

    }


}
