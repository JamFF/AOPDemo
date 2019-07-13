# 动态代理实现 AOP

除了 AspectJ，还可以使用动态代理实现 AOP

### 实现效果
在多个跳转界面，判断用户登录情况，进行不同的逻辑处理

### 动态代理优点

动态代理与静态代理相比较，最大的好处是接口中声明的所有方法都被转移到调用处理器一个集中的方法中处理（InvocationHandler.invoke）。

这样，在接口方法数量比较多的时候，我们可以进行灵活处理，而不需要像静态代理那样每一个方法进行中转，而且动态代理的应用使我们的类职责更加单一，复用性更强。 

动态代理，实现了代理者和委托者进行解耦，一个动态代理，可以代理多种委托，如果使用静态代理，一个代理类只能处理一个委托，程序规模稍大时就无法胜任了。

### 实现步骤

#### 一、委托类接口

JDK动态代理一定要针对接口。

首先从逻辑上讲，接口作为约束，只有相同的方法才可以实现代理。

其次从源码上看，动态代理在程序运行中，根据委托类接口来动态生成代理类的class文件，反编译生成的$Proxy0.class文件，可以看到$Proxy0继承了Proxy类，Java又不能多继承，所以只能依靠实现委托类接口的形式了。

```java
/**
 * description: 委托类接口
 * author: FF
 * time: 2019-07-03 11:40
 */
public interface ActivityManager {

    /**
     * 打开功能界面
     */
    void startActivity(Activity activity, @Constants.Activity int tag);
}
```

#### 二、委托类

需要实现接口

```java

/**
 * description: 委托类
 * author: FF
 * time: 2019-07-03 11:45
 */
public class ActivityManagerImpl implements ActivityManager {

    @Override
    public void startActivity(Activity activity, int tag) {
        Intent intent = new Intent(activity, MemberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MemberActivity.KEY, tag);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}
```

#### 三、动态代理处理类

用来创建动态代理实例的类

```java
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
        // 参数1，委托类的ClassLoader
        // 参数2，代理类要实现的接口，实际就是 new Class[]{ActivityManager.class}
        // 因为代理类和委托类实现的接口是一样的，所以也可以使用委托类的getInterfaces()来获取
        // 参数3，InvocationHandler 回调处理类
        return Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
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

```

#### 四、使用动态代理

1. 创建委托类的实例
2. 创建动态代理处理类
3. 创建动态代理的实例，相比委托类增加了判断登录的逻辑

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 跳转界面的代理
    private ActivityManager loginProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_me).setOnClickListener(this);
        findViewById(R.id.bt_pay).setOnClickListener(this);
        findViewById(R.id.bt_message).setOnClickListener(this);

        // 委托类的实例
        ActivityManagerImpl manager = new ActivityManagerImpl();
        // 创建动态代理处理类
        ProxyHandler proxyHandler = new ProxyHandler(this, manager);
        // 代理类的实例
        loginProxy = (ActivityManager) proxyHandler.getProxyInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_me:
                // 调用代理类的方法，相比委托类增加了判断登录的逻辑
                loginProxy.startActivity(this, Constants.ACTIVITY_ME);
                break;
            case R.id.bt_pay:
                loginProxy.startActivity(this, Constants.ACTIVITY_PAY);
                break;
            case R.id.bt_message:
                loginProxy.startActivity(this, Constants.ACTIVITY_MESSAGE);
                break;
        }
    }
}
```

### 参考

[为何jdk动态代理必须有接口，不支持仅实现类的代理](https://www.jianshu.com/p/ee9582c00eda)  
[Java基础加强总结(三)——代理(Proxy)](https://www.cnblogs.com/xdp-gacl/p/3971367.html)  
[java经典讲解-静态代理和动态代理的区别](https://blog.csdn.net/fangqun663775/article/details/78960545)  
[android设计模式之代理模式](https://www.jianshu.com/p/9b1fd124c881)  
[java的动态代理机制详解](https://www.cnblogs.com/xiaoluo501395377/p/3383130.html)