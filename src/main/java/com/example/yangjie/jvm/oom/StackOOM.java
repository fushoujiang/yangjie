package com.example.yangjie.jvm.oom;

/**
 * VM Args: -Xss2M
 * 栈溢出
 */
public class StackOOM {

    private void noStop(){
        while (true){
        }
    }


    public static void main(String[] args) {
        StackOOM stackOOM = new StackOOM();
        stackOOM.stackLeakByThread();
    }

    public  void  stackLeakByThread(){
        while (true){
            Thread thread = new Thread(()->{
                    noStop();
            });
            thread.start();
        }
    }
}
