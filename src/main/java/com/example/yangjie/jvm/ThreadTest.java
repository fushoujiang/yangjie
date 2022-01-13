package com.example.yangjie.jvm;

public class ThreadTest extends Thread {

    public static void main(String[] args) {

        ThreadTest mt1 = new ThreadTest("Thread a");
        ThreadTest mt2 = new ThreadTest("Thread b");

        mt1.setName("My-Thread-1 ");
        mt2.setName("My-Thread-2 ");

        mt1.start();
        mt2.start();
    }

    public ThreadTest(String name) {
    }

    public void run() {

        while (true) {

        }
    }


}
