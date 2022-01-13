package com.example.yangjie.lock;


import java.lang.annotation.*;

/**
 * 用于处理分布式锁，只要在方法上添加该注解 <br />
 * 系统会自动通过AOP对该方法加锁、释放锁
 *
 * keys的规则如下：<br />
 * 基础类型：String,Int,Long <br />
 * 引用类型：以.开头，格式为.field.field...
 *
 * @author hangduo -- 2017/12/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLockAnnotation {

    /**
     * 要加锁的key，有特定规则，和${@code keyIndexes}对应，用于解析keyIndex对应的值
     */
    String[] keys();

    /**
     * 要加锁的key的index，标识key所在的参数位置
     */
    int[] keyIndexes();


    FailBackMethodAnnotation failBackMethod();

    /**
     * lock 前缀
     */
    String lockPrefix() default "distributed_lock_";


    /**
     * 获取锁超时等待时间，单位为毫秒。
     *
     * <p>
     * 依赖具体分布式锁实现是否支持timeout
     * </p>
     */
    int timeout() default 0;

}
