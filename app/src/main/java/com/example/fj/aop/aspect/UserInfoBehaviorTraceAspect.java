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

    // 虽然实现了，但不推荐和BehaviorAspect的@Around一起使用，两个@Around不能保证执行顺序
    /*@Around("pointcut()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        if (true) {
            Log.d(TAG, "weaveJoinPoint: 已经登录");
            return joinPoint.proceed();
        } else {
            Log.d(TAG, "weaveJoinPoint: 跳转登录");
            return null;// 不调用
        }
    }*/

    // @Before和BehaviorAspect的@Around使用还是比较稳定
    @Before("pointcut()")
    public void jointPotin() {
        Log.d(TAG, "获取用户信息");
    }
}
