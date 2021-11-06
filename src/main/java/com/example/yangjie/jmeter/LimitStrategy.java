package com.example.yangjie.jmeter;

public interface LimitStrategy {
    default boolean limit() {
        return false;
    };
}
