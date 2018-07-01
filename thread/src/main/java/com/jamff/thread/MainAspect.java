package com.jamff.thread;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 描述：切面 切到主线程执行
 * 作者：JamFF
 * 创建时间：2018/6/9 17:10
 */
@Aspect
public class MainAspect {

    /**
     * 切入点，主线程可以返回所以把返回值改为*
     */
    @Pointcut("execution(@com.jamff.thread.annotation.Main * *(..))")
    public void methodAnnotationMain() {

    }

    /**
     * Around 围绕
     * After("methodAnnotationMain()")// 执行后调用
     * Before("methodAnnotationMain()")// 执行前调用
     */
    @Around("methodAnnotationMain()")
    public void doMainMethod(final ProceedingJoinPoint point) {

        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            try {
                point.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
