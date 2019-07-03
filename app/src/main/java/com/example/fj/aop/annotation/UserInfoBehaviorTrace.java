package com.example.fj.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 定义注解，用来标记用户信息切入点
 * author: FF
 * time: 2019-07-03 08:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface UserInfoBehaviorTrace {
}
