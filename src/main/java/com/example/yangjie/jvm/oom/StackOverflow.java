package com.example.yangjie.jvm.oom;

/**
 * VM Args: -Xss1024k
 * 栈溢出
 */
public class StackOverflow {

    int stackLength = 1;
    public static void main(String[] args) {
        StackOverflow stackOverflow = new StackOverflow();
        try {
            stackOverflow.stackLeak();
        }catch (Throwable e){
            System.out.println(stackOverflow.stackLength);
            e.printStackTrace();
        }
    }

    void stackLeak(){
        stackLength++;
        stackLeak();
    }
}
