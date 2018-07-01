package com.jamff.lib.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/30 11:45
 */
public class PermissionUtils {

    public static final int DEFAULT_REQUEST_CODE = 0xABC123;

    private static SimpleArrayMap<String, Integer> MIN_SDK_PERMISSION;

    static {
        MIN_SDK_PERMISSION = new SimpleArrayMap<>(8);
        MIN_SDK_PERMISSION.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSION.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSION.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSION.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSION.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSION.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSION.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSION.put("android.permission.WRITE_SETTINGS", 23);
    }

    /**
     * 检测是否需要请求权限
     *
     * @return false 需要 true 不需要
     */
    public static boolean hasPermission(Context context, String... permissions) {

        for (String permission : permissions) {
            if (permissionExists(permission) && !hasSelfPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测单个权限是否已经授权
     *
     * @return true 已授权 false 未授权
     */
    private static boolean hasSelfPermission(Context context, String permission) {
        try {
            return ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 如果在这个SDK版本存在的权限，则返回true
     */
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSION.get(permission);
        return minVersion == null || Build.VERSION.SDK_INT > minVersion;
    }

    /**
     * 请求权限结果
     *
     * @return true 成功，false失败
     */
    public static boolean verifyPermission(Context context, int... grantedResults) {
        if (grantedResults == null || grantedResults.length == 0) {
            return false;
        }

        for (int granted : grantedResults) {
            if (granted != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限失败后，判断是否勾选了不再提示
     *
     * @return true 未勾选，false 不再提示
     */
    public static boolean shouldShowRequestPermissionRational(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void invokAnnotation(Object obj, Class<? extends Annotation> annotationTypeClass) {

        // 获取切面上下文的类型
        Class<?> clz = obj.getClass();
        // 获取类型中的方法
        Method[] methods = clz.getDeclaredMethods();
        if (methods == null) {
            return;
        }
        for (Method method : methods) {
            // 获取该方法是否有注解
            if (method.isAnnotationPresent(annotationTypeClass)) {
                method.setAccessible(true);
                try {
                    method.invoke(obj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
