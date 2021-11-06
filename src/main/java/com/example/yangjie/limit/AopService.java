package com.example.yangjie.limit;

import org.springframework.stereotype.Component;

@Component
public class AopService implements AopInf {
    int count = 0 ;

    @Override
    @RateLimitAnnotation(isWait = false,perSecond=1,group = "AopService.test()",failBackMethod="testFailBack")
    public void test(){
        try {
            count++;
            Thread.sleep(100L);
            System.out.println("AopService 执行方法"+count);
        } catch (Exception e) {
            System.out.println("方法发生异常");
        }

    }
    public void testFailBack(){
        System.out.println("testFailBack 执行方法");
    }
    @Override
    public void test2(){
        try {
            System.out.println("AopService 执行方法");
            Thread.sleep(100);
        } catch (Exception e) {
            System.out.println("方法发生异常");
        } finally {
            System.out.println("AopService 执行方法 finally");
        }

    }

}
