package com.jamff.lib.permission.core;

import android.content.Context;
import android.util.Log;

import com.jamff.lib.permission.PermissionActivity;
import com.jamff.lib.permission.PermissionUtils;
import com.jamff.lib.permission.annotation.Permission;
import com.jamff.lib.permission.annotation.PermissionCanceled;
import com.jamff.lib.permission.annotation.PermissionDenied;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/30 15:01
 */
@Aspect
public class PermissionAspect {

    private static final String TAG = "JamFF";

    // 外部使用@Permission的地方就是切入点
    @Pointcut("execution(@com.jamff.lib.permission.annotation.Permission * *(..)) && @annotation(permission)")
    public void requestPermission(Permission permission) {

    }

    @Around("requestPermission(permission)")
    public void aroundJointPoint(final ProceedingJoinPoint joinPoint, Permission permission) {

        // 初始化Context
        Context context = null;

        final Object obj = joinPoint.getThis();
        if (obj instanceof Context) {
            context = (Context) obj;
        } else if (obj instanceof android.support.v4.app.Fragment) {
            context = ((android.support.v4.app.Fragment) obj).getActivity();
        } else if (obj instanceof android.app.Fragment) {
            context = ((android.app.Fragment) obj).getActivity();
        }

        if (context == null || permission == null) {
            Log.e(TAG, "aroundJointPoint: error");
            return;
        }

        final Context finalContext = context;

        PermissionActivity.requestPermission(context, permission.value(), permission.requestCode(), new IPermission() {
            @Override
            public void onGranted() {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onCanceled() {
                PermissionUtils.invokAnnotation(obj, PermissionCanceled.class);
            }

            @Override
            public void onDenied() {
                PermissionUtils.invokAnnotation(obj, PermissionDenied.class);
                // PermissionUtils.goToMenu(finalContext);
            }
        });
    }
}
