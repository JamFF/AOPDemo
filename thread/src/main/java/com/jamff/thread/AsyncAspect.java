package com.jamff.thread;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述：切面 切到子线程执行
 * 作者：JamFF
 * 创建时间：2018/6/9 16:48
 */
@Aspect
public class AsyncAspect {

    /**
     * 切入点，所有被Async声明的为子线程，一般无返回值，使用void的函数
     */
    @Pointcut("execution(@com.jamff.thread.annotation.Async void *(..))")
    public void methodAnnotationAsync() {

    }

    /**
     * Around 围绕
     * After("methodAnnotationAsync()")// 执行后调用
     * Before("methodAnnotationAsync()")// 执行前调用
     */
    @Around("methodAnnotationAsync()")
    public void doAsyncMethod(final ProceedingJoinPoint point) {

        Disposable disposable = Schedulers.io().scheduleDirect(() -> {
            try {
                point.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        MainActivity.sDisposable.add(disposable);
    }
}
