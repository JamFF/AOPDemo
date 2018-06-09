package com.jamff.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述：切面 切到子线程的代码
 * 作者：JamFF
 * 创建时间：2018/6/9 16:48
 */
@Aspect
public class AsyncAspect {

    /**
     * 切入点，所有被Async声明的为子线程，一般无返回值，使用void的函数
     */
    @Pointcut("execution(@com.jamff.aop.annotation.Async void *(..))")
    public void methodAnnotationAsync() {

    }

    /**
     * Around 围绕
     * After("methodAnnotationAsync()")// 执行后调用
     * Before("methodAnnotationAsync()")// 执行前调用
     *
     * @param point
     */
    @Around("methodAnnotationAsync()")
    public void doAsyncMethod(final ProceedingJoinPoint point) {

        Disposable disposable = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                // 调用真实方法
                try {
                    point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();

        MainActivity.sDisposable.add(disposable);
    }
}
