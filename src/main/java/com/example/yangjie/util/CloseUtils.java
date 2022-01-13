package com.example.yangjie.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭工具类
 * @author wenqi.wu
 */
public class CloseUtils {
    private static final Logger logger = LoggerFactory.getLogger(CloseUtils.class);

    /**
     * 关闭资源
     * @param closeable closeable
     */
    public static void close(Closeable closeable){
        if (closeable == null) return;
        try {
            closeable.close();
        }
        catch (IOException ex) {
            assert true;  // avoid an empty catch
        }
    }
}
