package com.example.fj.aop.aspect;

import android.util.Log;

import com.example.fj.aop.annotation.BehaviorTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 描述：用户行为统计的切面
 * 作者：JamFF
 * 创建时间：2017/4/15 16:42
 */
@Aspect
public class BehaviorAspect {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static final String TAG = "JamFF";

    // 这个切面，由哪些方法组成，* *(..)代表任意类的任意方法任意无限参数
    // 不需要实现，只是个标记，定义切面
    @Pointcut("execution(@com.example.fj.aop.annotation.BehaviorTrace * *(..))")
    public void methodAnnotatedWithBehaviorTrace() {}

    // @Before("methodAnnotatedWithBehaviorTrace()")// 执行前调用
    // @After("methodAnnotatedWithBehaviorTrace()")// 执行后调用
    @Around("methodAnnotatedWithBehaviorTrace()")// 执行前后调用
    public Object waveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取注解上的功能名称
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        BehaviorTrace behaviorTrace = methodSignature.getMethod().getAnnotation(BehaviorTrace.class);
        String funcName = behaviorTrace.value();

        long begin = System.currentTimeMillis();

        // 调用该方法才会执行@BehaviorTrace("xxx")方法内的代码
        Object ret = joinPoint.proceed();

        long duration = System.currentTimeMillis() - begin;

        String time = sdf.format(new Date());

        Log.d(TAG, String.format("时间：%s，功能：%s执行，耗时：%d", time, funcName, duration));

        return ret;
    }
}
