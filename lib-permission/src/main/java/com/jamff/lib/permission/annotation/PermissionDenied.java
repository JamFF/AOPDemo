package com.jamff.lib.permission.annotation;

import com.jamff.lib.permission.PermissionUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：授权失败，并勾选不再提示
 * 作者：JamFF
 * 创建时间：2018/6/30 14:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionDenied {

    int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
}
