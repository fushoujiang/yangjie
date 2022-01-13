package com.example.yangjie.id.idworker;

import com.example.yangjie.util.IPUtils;
import com.example.yangjie.util.JVMUtils;
import com.example.yangjie.util.NetUtils;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于主机host和jvm进程号+时间戳生成全局唯一ID
 * @author wenqi.wu
 */
public class HostPIdAndTimestampIdGenerator implements IdGenerator<String>{
    private static final Logger logger = LoggerFactory.getLogger(HostPIdAndTimestampIdGenerator.class);
    private final static String PADDING_CHAR = "0";
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 999;
    private final static int JOINER_IP_MAX_LENGTH = 10;
    private final static int JOINER_PID_MAX_LENGTH = 5;
    private final Object mutex = new Object();
    private AtomicInteger incrementer;
    private long ip;
    private int pid;

    public HostPIdAndTimestampIdGenerator(){
        (new Random()).nextInt(MAX_VALUE);
        this.incrementer = new AtomicInteger((new Random()).nextInt(MAX_VALUE));
        this.ip = IPUtils.ipToLong(NetUtils.getLocalHost());
        this.pid = JVMUtils.getPid();
    }
    @Override
    public void init() {
        logger.info("init host pid and timestamp id generator ok,the ip is {},the pid is {}",ip,pid);
    }

    @Override
    public String generateNextId() {
        StringBuilder seqId = new StringBuilder();
        String ipString = String.valueOf(ip);
        String pidString = String.valueOf(pid);
        seqId.append(Strings.repeat(String.valueOf(PADDING_CHAR), JOINER_IP_MAX_LENGTH - ipString.length())).append(ipString);
        seqId.append(Strings.repeat(String.valueOf(PADDING_CHAR), JOINER_PID_MAX_LENGTH - pidString.length())).append(pidString);
        seqId.append(System.currentTimeMillis());
        int x = getNextIdValue();
        if(x < 10){
            seqId.append("00");
        }else if(x < 100){
            seqId.append("0");
        }
        seqId.append(x);
        return seqId.toString();
    }
    /**
     * 获取下一个自增序列
     * @return  int
     */
    private int getNextIdValue(){
        int current = incrementer.getAndIncrement();
        synchronized (mutex){
            if(incrementer.get() > MAX_VALUE){
                incrementer.set(MIN_VALUE);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //ignored
            }
        }
        return current;
    }

    @Override
    public void destroy() {

    }
}
