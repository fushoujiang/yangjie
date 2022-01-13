package com.example.yangjie.jvm.oom;


import java.util.ArrayList;
import java.util.List;

/**
 * VM Args:-Xms20m -Xmx20m -XX:HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/fu/Downloads/heapdump.hprof
 * 堆内存溢出
 */
public class HeapOOM {

    private static class HeapOomObject {
        int[] ints =new int[1024*1024];
    }
    public static void main(String[] args) {
        List<HeapOomObject> list = new ArrayList<HeapOomObject>();
        while (true) {
            list.add(new HeapOomObject());
        }
    }
}
