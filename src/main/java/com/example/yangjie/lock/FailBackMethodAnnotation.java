package com.example.yangjie.lock;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FailBackMethodAnnotation {
    /**
     * 失败之后执行本类的方法名,若无则抛异常
     * <p>
     *     入参和返回值和增强方法需保持一致
     * </p>
     */
    String failBackMethod() default "";

}
