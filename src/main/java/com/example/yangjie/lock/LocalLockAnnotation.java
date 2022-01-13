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
 * @see
 * @author zhangduo -- 2017/12/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalLockAnnotation {


    /**
     * 锁标识若不配置则走默认
     */
    String lockKey() ;
    /**
     * 失败回滚方法
     */
    FailBackMethodAnnotation failBackMethod();
    /**
     * 获取锁超时等待时间，单位为毫秒。
     *
     * <p>
     * 依赖具体分布式锁实现是否支持timeout
     * </p>
     */
    int timeout() default 0;

}
