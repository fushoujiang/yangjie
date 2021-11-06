package com.example.yangjie.limit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @see RateLimitAop
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimitAnnotation {

    /**
     * 限流的分组
     * @return
     */
    String group() default "";


    boolean isWait() default false;

    /**
     * 每秒向桶中放入令牌的数量   默认最大即不做限流
     *
     * @return
     */
    int perSecond() default Integer.MAX_VALUE;

    /**
     * 获取令牌的等待时间  默认0
     *
     * @return
     */
    int timeOut() default 0;
    /**
     * 超时时间单位
     *
     * @return
     */
    TimeUnit timeOutUnit() default TimeUnit.MILLISECONDS;


    /**
     * 失败之后执行本类的方法名,若无则抛异常
     * <p>
     *     入参和返回值和增强方法需保持一致
     * </p>
     */
    String failBackMethod() default "";
}