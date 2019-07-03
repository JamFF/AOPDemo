package com.example.fj.aop.aspect;

import android.util.Log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * description: 获取用户信息的切面
 * author: FF
 * time: 2019-07-03 08:45
 */
@Aspect
public class UserInfoBehaviorTraceAspect {

    private static final String TAG = "JamFF";

    @Pointcut("execution(@com.example.fj.aop.annotation.BehaviorTrace * *(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public Object weaveJoinPoint() {
        Log.d(TAG, "获取用户信息");
        return null;
    }
}
