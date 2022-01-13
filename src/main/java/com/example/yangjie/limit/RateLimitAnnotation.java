package com.example.yangjie.limit;
import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimitAnnotation {
    /**
     * 限流项目名称
     *
     * @return
     */
    String project() default "default_project_name";

    /**
     * 限流的分组
     *
     * @return
     */
    String group() default "";
    /**
     * <p>
     * true：com.google.common.util.concurrent.RateLimiter#acquire()
     * false：com.google.common.util.concurrent.RateLimiter#tryAcquire(long, java.util.concurrent.TimeUnit)
     * </p>
     *
     * @return
     */
    boolean isWait() default false;

    /**
     * 每秒向桶中放入令牌的数量   默认最大即不做限流
     *
     * @return
     */
    int perSecond() default Integer.MAX_VALUE;

    /**
     * 失败之后执行本类的方法名,若无则抛异常,强烈建议熔断->降级
     * <p>
     * 入参和返回值和增强方法需保持一致
     * </p>
     */
    String failBackMethod() default "";

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

}