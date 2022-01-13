package com.example.yangjie.thread;

import java.util.concurrent.LinkedBlockingQueue;

public class FutureTaskTest {
    public static final LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<String >(1);

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                for (;;) {
                    linkedBlockingQueue.put("fj");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                for(;;){
                    linkedBlockingQueue.put("fjs");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            for (;;){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String  poll = linkedBlockingQueue.poll();
            }
        }).start();

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
