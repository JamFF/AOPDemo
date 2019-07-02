# 性能检测

统计方法的耗时

### 集成AspectJ

1. 使用AspectJ的编译器

    **不推荐写在项目的 build.gradle 中**，该配置属于 module 中
    ```groovy
    // 编译脚本，这里配置的东西是给gradle用的
    buildscript {
        // 仓库
        repositories {
            // 从jcenter取得依赖组件
            jcenter()
        }
        // 依赖
        dependencies {
            // 使用AspectJ的编译器
            classpath 'org.aspectj:aspectjtools:1.9.1'
            // 没有使用到该功能，可以不引入weaver
            // classpath 'org.aspectj:aspectjweaver:1.9.1'
        }
    }
    ```

2. 添加 AspectJ 的依赖

    ```groovy
    dependencies {
        implementation 'org.aspectj:aspectjrt:1.9.1'
    }
    ```

3. 添加 AspectJ 插件代码

    这段代码调用有两个作用：
    * 在编译期自动生成代码
    * 增加 log 打印

    ```groovy
    import org.aspectj.bridge.IMessage
    import org.aspectj.bridge.MessageHandler
    import org.aspectj.tools.ajc.Main
    
    // project代表当前这个文件，logger是日志组件
    final def log = project.logger
    // 变体
    final def variants = project.android.applicationVariants
    // 在构建工程，执行编辑遍时历变体
    variants.all {
        variant ->
            if (!variant.buildType.isDebuggable()) {
                log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                return
            }
            // 拿到Java编译任务
            JavaCompile javaCompile = variant.javaCompile
            // 在Java编译之后执行
            javaCompile.doLast {
                // 录入参数
                String[] args = ["-showWeaveInfo",
                                 // aspectj的版本
                                 "-1.9",
                                 // 采集所有class文件的路径
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 // AspectJ编译器的classpath
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 // 输出目录，AspectJ处理完成后的输出目录
                                 "-d", javaCompile.destinationDir.toString(),
                                 // Java程序的类查找路径
                                 "-classpath", javaCompile.classpath.asPath,
                                 // 覆盖引导类的位置，android中使用android.jar而不是jdk
                                 "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
                log.debug "ajc args: " + Arrays.toString(args)
    
                MessageHandler handler = new MessageHandler(true)
                new Main().run(args, handler)
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break
                        case IMessage.WARNING:
                            log.warn message.message, message.thrown
                            break
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break
                    }
                }
            }
    }
    ```

4. 添加注解类

    使用注解的目的就是为了用来标记**切入点**，可以在切面中通过表达式找到切入点。

    ```java
    /**
     * description: 定义注解，用来标记切入点
     * author: FF
     * time: 2019-07-02 15:45
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface BehaviorTrace {
        String value();
    }
    ```
    
    元注解Retention，表明注解的保留策略，也就是注解的生命周期：
    * SOURCE：最短，在源文件（java）和编译期间，编译完成后，class文件中不包含。
    * CLASS：默认策略，保留到class文件，编译成dex文件时舍弃，所以运行时无法获得。
    * RUNTIME：最长，保留到运行时期，也会在Class字节码文件中存在，可反射获取。
    
    这里如何选择呢？
    
    AspectJ 的原理是在编译期自动生成一些代码，写到 class 文件中，所以明确 `SOURCE` 肯定是不合适的。
    
    至于 `CLASS` 和 `RUNTIME` 的选择，就要看使用中，是否需要反射获取信息，大多情况下都需要通过注解传递一些信息，所以这里使用 `RUNTIME`。

5. 使用AspectJ

    ```java
    /**
     * description: 切面
     * author: FF
     * time: 2019-07-02 16:26
     */
    @Aspect
    public class BehaviorAspect {
    
        private static final String TAG = "JamFF";
    
        // @Pointcut 标记切入点，方法名随意，但是要与下面@Around中的保持一直，方法空实现就可以
        // 格式：@Pointcut("execution(@注解全类名 表达式)")
        // 表达式用来指定使用注解的地方，哪个类的哪个方法
        // 表达式 * *(..) 的含义：第一个*任意类，第二个*任意方法，(..)任意参数
        @Pointcut("execution(@com.example.fj.aop.annotation.BehaviorTrace * *(..))")
        public void methodAnnotatedWithBehaviorTrace() {
        }
    
        // @Before 执行切入点前调用
        // 参数JoinPoint是连接点，可以通过其获取注解参数和使用注解的方法信息
        // 如果不需要获取这些信息，可以省略JoinPoint参数
        /*@Before("methodAnnotatedWithBehaviorTrace()")
        public void beforeJoinPoint(JoinPoint joinPoint) throws Throwable {
    
            // 获取连接点的签名
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
    
        // @After 执行切入点后调用，使用方式与 @Before 一致
        // 这里使用无参的方式
        /*@After("methodAnnotatedWithBehaviorTrace()")
        public void afterJoinPoint() throws Throwable {
            Log.d(TAG, "@After ");
        }*/
    
        // @Around 在切入点前后都加入代码
        // 该模式下，参数必须存在，并且为ProceedingJoinPoint类型，必须有返回值
        @Around("methodAnnotatedWithBehaviorTrace()")
        public Object aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    
            Log.d(TAG, "@Around: start");
    
            // 获取连接点的签名
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
    ```
    
    * `@Aspect` 标记切面
    * `@Pointcut` 标记切入点，表达式可以通过注解找到切入点，`@Pointcut("execution(@注解全类名 表达式)")`
    * `@Around` 环绕通知，在目标执行中执行通知
    * `@Before` 前置通知，在目标执行之前执行通知
    * `@After` 后置通知，目标执行后执行通知
    
6. 使用注解标记

    ```java
    @BehaviorTrace("摇一摇")
    private void mShake() {
        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));
    }
    
    @BehaviorTrace("语音消息")
    private void mAudio(int i) {
        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));
    }
    
    @BehaviorTrace("视频通话")
    private void mVideo(int i, int j) {
        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));
    }
    ```
    
### 注意事项

1. 目前最新版本 1.9.4，最低支持 `minSdkVersion 24`
2. 一个方法上只能使用一个通知注解，不然重复切入，不能确定执行结果，并且可能出现错误
3. 虽然可以定义多个方法，使用多种通知注解，但是不推荐，在同时使用多种通知注解时，会出现不确定的执行结果
4. 同时使用Before和After是不冲突的，但是推荐使用 `@Around` 更方便，并且便于交互
5. 编译后，可以在 `build/intermediates/javac/debug/compileDebugJavaWithJavac/classes` 目录下找到编译后的 class 文件，会发现切入点的代码被修改了，这就是 AspectJ 帮我们做的事情
    ```java
    @BehaviorTrace("摇一摇")
    private void mShake() {
        JoinPoint var1 = Factory.makeJP(ajc$tjp_0, this, this);
        mShake_aroundBody1$advice(this, var1, BehaviorAspect.aspectOf(), (ProceedingJoinPoint)var1);
    }

    @BehaviorTrace("语音消息")
    private void mAudio(int i) {
        JoinPoint var3 = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(i));
        mAudio_aroundBody3$advice(this, i, var3, BehaviorAspect.aspectOf(), (ProceedingJoinPoint)var3);
    }

    @BehaviorTrace("视频通话")
    private void mVideo(int i, int j) {
        JoinPoint var5 = Factory.makeJP(ajc$tjp_2, this, this, Conversions.intObject(i), Conversions.intObject(j));
        mVideo_aroundBody5$advice(this, i, j, var5, BehaviorAspect.aspectOf(), (ProceedingJoinPoint)var5);
    }
    ```
    
### 参考

[AspectJ 历史版本](http://jcenter.bintray.com/org/aspectj/aspectjrt/)  
[Android基于AOP的非侵入式监控之——AspectJ实战](https://blog.csdn.net/woshimalingyi/article/details/51476559)  
[AspectJ 切面注解中五种通知注解：@Before、@After、@AfterRunning、@AfterThrowing、@Around](https://blog.csdn.net/u010502101/article/details/78823056)