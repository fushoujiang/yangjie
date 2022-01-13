package com.example.yangjie.jvm.oom;

import java.nio.ByteBuffer;

/**
 * VM Args:-Xms20m -Xmx20m  -XX:MaxDirectMemorySize=10M
 */
public class DirectMemoryOOM {
    private static final int _1MB = 1024*1024;
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ByteBuffer.allocateDirect(30*1024*1024);
    }
}
