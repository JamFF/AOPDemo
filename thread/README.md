# 性能检测

实现线程切换

### 集成AspectJ

1. 使用AspectJ的编译器

    ```groovy
    // 编译脚本，这里配置的东西是给gradle用的
    buildscript {
        // 仓库
        repositories {
            // 从maven取得依赖组件
            mavenCentral()
        }
        // 依赖
        dependencies {
            // 使用AspectJ的编译器
            classpath 'org.aspectj:aspectjtools:1.9.1'
        }
    }
    ```

2. 添加AspectJ的依赖

    ```groovy
    dependencies {
        implementation 'org.aspectj:aspectjrt:1.9.1'
    }
    ```

3. 添加插件代码

    ```groovy
    import org.aspectj.tools.ajc.Main
    
    /*
    因为上面是
    android {
        ...
    }
    所以这里是project.android
    
    因为apply plugin: 'com.android.application'是application
    所以这里是applicationVariants
    
    如果是apply plugin: 'com.android.library'，要改为libraryVariants
    
    .all是拿到两个变体，一个是debug一个是release
    */
    project.android.applicationVariants.all {
        // it代表了debug或者release
        JavaCompile javaCompile = it.javaCompile
        // 编译任务执行完成之后 工作
        javaCompile.doLast {
            // 这里已经编译出来.class文件了
            String[] args = [
                    // 使用Java8
                    "-1.8",
                    // AspectJ处理的源文件，也就是.class
                    // destinationDir目录是aop\build\intermediates\classes
                    "-inpath", javaCompile.destinationDir.toString(),
                    // AspectJ编译器的classpath
                    "-aspectpath", javaCompile.classpath.asPath,
                    // 输出目录，AspectJ处理完成后的输出目录
                    "-d", javaCompile.destinationDir.toString(),
                    // Java程序的类查找路径
                    "-classpath", javaCompile.classpath.asPath,
                    // 覆盖引导类的位置，android中使用android.jar而不是jdk
                    "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
    
            // 调用执行AspectJ编译器
            new Main().runMain(args, false)
        }
    }
    ```

4. 添加注解类

    * 运行在异步线程

        ```java
        @Retention(RetentionPolicy.CLASS)
        @Target(ElementType.METHOD)
        public @interface Async {
        }
        ```
        
    * 运行在主线程
    
        ```java
        @Retention(RetentionPolicy.CLASS)
        @Target(ElementType.METHOD)
        public @interface Main {
        }
        ```

5. 使用AspectJ

    * 切面 切到子线程执行
    
        ```java
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
        ```
        
    * 切面 切到主线程执行
    
        ```java
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
        ```
        
6. 使用注解

    ```java
    @Async
    private void writeFile() {
        Log.d(TAG, "Write File: " + Thread.currentThread());
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 如果线程没有处理完，调用sDisposable.clear()，showResult会立即执行
        showResult();
    }
 
    @Main
    private void showResult() {
        Log.d(TAG, "更新UI: " + Thread.currentThread());
        Toast.makeText(this, "更新UI：" + Thread.currentThread(), Toast.LENGTH_SHORT).show();
    }
    ```