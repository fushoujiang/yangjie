package com.example.yangjie.unsaft;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class FenceTest{
    public    boolean isBreak;
    public    boolean test;

    public boolean getBreak() {
        return isBreak;
    }
    public void toBreak() {
        isBreak = true;
        test = true;
        System.out.println("中断标志位isBreak:" + isBreak);
    }
    /**
     * 业务处理线程
     */
    public class LoopThread extends Thread {
        @Override
        public void run() {
            System.out.println("开始处理业务，当isShutdown置为false停止");
            while (!isBreak) {
                unsafe().loadFence();
            }

            System.out.println("处理业务结束");
        }
    }
    /**
     * 中断线程
     */
    public class BreakThread extends Thread {
        @Override
        public void run() {
            toBreak();
        }
    }
    public static void main(String[] args) {
        try {
            FenceTest example = new FenceTest();
            example.new LoopThread().start();
            Thread.sleep(1000);
            example.new BreakThread().start();

            Thread.sleep(1000);
            System.out.println("中断标志位:" + example.getBreak());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    static Unsafe unsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
