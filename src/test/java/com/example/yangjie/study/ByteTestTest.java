package com.example.yangjie.study; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
* ByteTest Tester. 
* 
* @author <Authors name> 
* @since <pre>9月 2, 2021</pre> 
* @version 1.0 
*/ 
public class ByteTestTest {
    ByteTest x = new ByteTest();

    private volatile  int atomicInteger = 0;
@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: sum(int i, int j) 
* 
*/ 
@Test
public void testSum() throws Exception { 
//TODO: Test goes here...
    int b = x.sum(1,2);
    System.out.println(b);
    assert b==3;
}

@Test
public void testString() throws InterruptedException {
    Thread.currentThread().getThreadGroup().setDaemon(true);

    /**
     * 就写思路，就说方法名字记不太清了
     * 1、先将string用逗号切割成数组
     * 2、新建一个list集合，来存放不重复的string字符串
     * 3、遍历数据，将集合中不包含的数据添加进去
     * 4、list集合中就是不重复的字符串
     */
    String s = "5,1,1,1,2,2,3,3,4";
    String[] strings =  s.split(",");
    ArrayList list = new ArrayList();
    for(int i =0;i<strings.length;i++){
        if (list.contains(strings[i])){

        }else {
            list.add(strings[i]);
        }
    }
    System.out.println(list);
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 1/0;
        }
    });

    Thread thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            for (;;){
                if (atomicInteger==2){
                    System.out.println("111");
                }
            }

        }
    });

    thread2.start();
    thread.start();
    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(t.toString());
            System.out.println(e.toString());
        }
    });
    Thread.sleep(1000L);



}




} 
