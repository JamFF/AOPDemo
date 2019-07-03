package com.ff.proxy;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * description: 动态代理的处理类，打开界面前检测登录状态
 * author: FF
 * time: 2019-07-03 16:07
 */
public class ProxyHandler implements InvocationHandler {

    private static final String TAG = "ProxyHandler";

    private WeakReference<Activity> mReference;
    private Object obj;// 委托类的实例

    public ProxyHandler(Activity activity, Object targetObject) {
        mReference = new WeakReference<>(activity);
        obj = targetObject;
    }

    /**
     * 创建动态代理对象
     *
     * @return 返回一个代理实例
     */
    public Object getProxyInstance() {
        // 参数1，代理类的ClassLoader
        // 参数2，代理类要实现的接口，实际就是 new Class[]{ActivityManager.class}
        // 因为代理类和委托类实现的接口是一样的，所以也可以使用委托类的getInterfaces()来获取
        // 参数3，InvocationHandler 回调处理类
        return Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                obj.getClass().getInterfaces(), this);
    }

    /**
     * 拦截委托类中即将执行的方法
     *
     * @param proxy  代理类的实例
     * @param method 委托类中被调用的方法
     * @param args   被调用方法的参数
     * @return 委托类方法的返回值
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Type t = method.getGenericReturnType();
        Log.d(TAG, method.getName() + " 返回值类型: " + t);

        // 注意：需要定义与该方法匹配的返回值类型
        Object result = null;
        if (mReference != null && mReference.get() != null) {
            Activity activity = mReference.get();
            // 委托类接口里面可能有多个方法，要找到需要代理的方法
            if ("startActivity".equals(method.getName())) {
                if (SPUtil.getBooleanSp(SPUtil.IS_LOGIN, activity)) {
                    // 已登录
                    try {
                        result = method.invoke(obj, args);// 调用委托类中的原方法
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "invoke: error", e);
                        throw e;
                    }
                } else {
                    // 未登录
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                }
            }
        }
        return result;
    }
}
