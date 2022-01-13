package com.example.yangjie.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * JVM的工具类
 * @author wenqi.wu
 */
public final class JVMUtils {

    private static final Logger logger = LoggerFactory.getLogger(JVMUtils.class);
    public final static int PID_MAX_LENGTH = 5;
    private static int PID = -1;
    private JVMUtils(){}

    /***
     * 获取java的进程ID
     * @return  int
     */
    public static int getPid() {
        if (PID < 0) {
            try {
                RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
                String name = runtime.getName(); // format: "pid@hostname"
                PID = Integer.parseInt(name.substring(0, name.indexOf('@')));
            } catch (Throwable e) {
                PID = 0;
            }
        }
        return PID;
    }
}
