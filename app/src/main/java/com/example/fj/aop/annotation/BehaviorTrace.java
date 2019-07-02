package com.example.fj.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 定义注解，用来标记切入点
 * author: FF
 * time: 2019-07-02 15:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BehaviorTrace {
    String value();
}
