package com.jamff.lib.permission.annotation;

import com.jamff.lib.permission.PermissionUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：请求授权
 * 作者：JamFF
 * 创建时间：2018/6/30 14:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {

    String[] value();

    int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
}
