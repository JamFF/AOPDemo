package com.jamff.aop;

import android.os.Handler;
import android.os.Looper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/9 17:10
 */
@Aspect
public class MainAspect {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 切入点，主线程可以返回所以把返回值改为*
     */
    @Pointcut("execution(@com.jamff.aop.annotation.Main * *(..))")
    public void methodAnnotationMain() {

    }

    /**
     * Around 围绕
     * After("methodAnnotationAsync()")// 执行后调用
     * Before("methodAnnotationAsync()")// 执行前调用
     *
     * @param point
     */
    @Around("methodAnnotationMain()")
    public void doMainMethod(final ProceedingJoinPoint point) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
