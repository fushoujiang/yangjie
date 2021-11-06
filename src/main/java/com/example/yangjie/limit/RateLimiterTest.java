package com.example.yangjie.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RateLimiterTest {
    private static RateLimiter limiter = RateLimiter.create(500);
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            boolean b = true;
            limiter.acquire();
//           b = limiter.tryAcquire(2);
            if (b){
                test(i);
            }else {
                System.out.println(i+"等待，时间 = " +FORMATTER.format(new Date()));
            }

        }

    }

    private static void test(int i){
        System.out.println(i+"获取到了令牌，时间 = " +FORMATTER.format(new Date()));

    }
}
