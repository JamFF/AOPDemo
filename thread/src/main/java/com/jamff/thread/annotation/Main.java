package com.jamff.thread.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：主线程注解
 * 作者：JamFF
 * 创建时间：2018/6/9 16:42
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Main {
}
