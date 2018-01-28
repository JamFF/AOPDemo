# AOPDemo
使用AspectJ实现AOP编程

## AOP编程的使用场景
比如权限验证，100个功能，其中有10个需要登录或会员才能使用，可以在这十个方法执行前使用，如果没有登录跳转到登录或充值界面

比如操作文件，释放资源，可以在所有方法执行后调用

比如性能检测，在方法执行前、执行后调用

## AOP编程的优点
降低耦合度，统一调用，便于维护


## AspectJ的使用步骤

1.使用AspectJ的编译器

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // 使用aspectj的编译器
        classpath 'org.aspectj:aspectjtools:1.8.10'
        classpath 'org.aspectj:aspectjweaver:1.8.10'
    }
}
```

2.添加AspectJ的jar包

```groovy
dependencies {
    // aspectj的jar包
    compile 'org.aspectj:aspectjrt:1.8.10'
}
```

3.添加插件代码，调用后有输出

```groovy
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

final def log = project.logger
final def variants = project.android.applicationVariants

variants.all { variant ->
    if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return;
    }

    JavaCompile javaCompile = variant.javaCompile
    javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.8",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler);
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break;
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break;
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break;
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break;
            }
        }
    }
}
```

4.添加注解类

```java
/**
 * 描述：标志需要进行用户行为统计
 * 作者：JamFF
 * 创建时间：2017/4/15 16:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BehaviorTrace {

    String value();
}
```

5.使用注解

```java
@BehaviorTrace("摇一摇")
private void mShake() {

    // 模拟网络延迟
    SystemClock.sleep(new Random().nextInt(3000));
}

@BehaviorTrace("语音消息")
private void mAudio() {

    // 模拟网络延迟
    SystemClock.sleep(new Random().nextInt(3000));
}

@BehaviorTrace("视频通话")
private void mVideo() {

    // 模拟网络延迟
    SystemClock.sleep(new Random().nextInt(3000));
}
```

6.使用AspectJ，在方法执行前后添加计时操作

```java
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
```