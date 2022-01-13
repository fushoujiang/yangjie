package com.example.yangjie.limit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationTest {
    public static void main(String[] args) {
        System.out.println("项目启动");
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(Appconfig.class);
        ac.refresh();
        AopInf bean = ac.getBean(AopInf.class);
        for (int i = 0; i < 100; i++) {
            bean.test("FSJ"+i);
        }
        System.out.println("项目结束");

    }
}
