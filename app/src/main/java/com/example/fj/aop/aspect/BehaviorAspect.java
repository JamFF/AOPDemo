package com.example.fj.aop.aspect;

import android.content.Context;
import android.util.Log;

import com.example.fj.aop.annotation.BehaviorTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * description: 统计方法执行时间切面
 * author: FF
 * time: 2019-07-02 16:26
 */
@Aspect
public class BehaviorAspect {

    private static final String TAG = "JamFF";

    // @Pointcut 标记切入点，方法名随意，但是要与下面@Around中的保持一直，方法空实现就可以
    // 格式：@Pointcut("execution(@注解全类名 表达式)")
    // execution：以方法执行时作为切点，触发Aspect类
    // 表达式：@用来声明要找注解，全类名指定哪个注解，再后面指定了哪个类的哪个方法
    // 表达式 * *(..) 的含义：第一个*任意类，第二个*任意方法，(..)任意参数
    @Pointcut("execution(@com.example.fj.aop.annotation.BehaviorTrace * *(..))")
    public void methodAnnotatedWithBehaviorTrace() {
    }

    // @Before 执行切入点前调用，方法名随意
    // 参数JoinPoint是连接点，可以通过其获取注解参数和使用注解的方法信息
    // 如果不需要获取这些信息，可以省略JoinPoint参数
    /*@Before("methodAnnotatedWithBehaviorTrace()")
    public void beforeJoinPoint(JoinPoint joinPoint) {

        // 获取连接点的方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取类名
        String className = signature.getDeclaringType().getSimpleName();
        // 获取方法名
        String methodName = signature.getName();
        // 获取参数列表
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        // 获取注解实例
        BehaviorTrace behaviorTrace = signature.getMethod().getAnnotation(BehaviorTrace.class);
        // 获取注解中的值
        String funcName = behaviorTrace.value();

        Log.d(TAG, String.format("@Before %s类的%s方法，参数%s，功能：%s",
                className, methodName, args, funcName));
    }*/

    // @After 执行切入点后调用，方法名随意，使用方式与 @Before 一致
    // 这里使用无参的方式
    /*@After("methodAnnotatedWithBehaviorTrace()")
    public void afterJoinPoint() {
        Log.d(TAG, "@After ");
    }*/

    // @Around 在切入点前后都加入代码，方法名可以任意取
    // 该模式下，参数必须存在，并且为ProceedingJoinPoint类型，必须有返回值
    @Around("methodAnnotatedWithBehaviorTrace()")
    public Object aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        Log.d(TAG, "@Around: start");

        // 可以通过该方式获取上下文
        Context context = (Context) joinPoint.getThis();

        // 获取连接点的方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取注解实例
        BehaviorTrace behaviorTrace = methodSignature.getMethod().getAnnotation(BehaviorTrace.class);
        // 获取注解中的值
        String funcName = behaviorTrace.value();

        long begin = System.currentTimeMillis();

        // 执行BehaviorTrace注解的方法内的代码
        Object ret = joinPoint.proceed();

        long duration = System.currentTimeMillis() - begin;

        Log.d(TAG, String.format("@Around: end 功能：%s执行，耗时：%d", funcName, duration));

        return ret;// 注意返回值
    }
}
