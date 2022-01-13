package com.example.yangjie.id;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 核心算法(基于snowflake生成器)
 * 生成64bits的long数字
 */
public class IdWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdWorker.class);
    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private final static long twepoch = 1288834974657L;//twitter第一版发布时间戳
    private final static long workerIdBits = 4L;
    private final static long datacenterIdBits = 5L;
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);// 16
    private final static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);// 31
    private final static long sequenceBits = 5L;
    private final static long workerIdShift = sequenceBits;// 5
    private final static long datacenterIdShift = sequenceBits + workerIdBits;// 9
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;// 14
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);//32
    private long lastTimestamp = -1L;

    //构造器
    public IdWorker(final long workerId, final long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取生成下一个ID
     * @return  long
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            LOGGER.error(String.format("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp));
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 最后12位如果都为1了，说明计数器满了，则等到下一秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 获取时间戳
     * @param lastTimestamp lastTimestamp
     * @return  long
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前系统时间
     * @return  long
     */
    private long timeGen() {
        return System.currentTimeMillis();
//        return SystemClock.now();
    }

    public static final ThreadPoolExecutor dsfExecutor = new ThreadPoolExecutor(3,
            3 ,
            1000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("grab-dsf-caller-%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());
    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        IdWorker worker  = new IdWorker(1,1);
        int size =3000000 ;
        List<Callable<Long>> callables = new ArrayList<>(size);
        for (int i=0;i<size;i++){
            Callable<Long> callable = new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return worker.nextId();
                }
            };
            callables.add(callable);
        }
        try {
            List<Future<Long>> futures = dsfExecutor.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println(end-begin);

    }
}
