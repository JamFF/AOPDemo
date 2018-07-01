# 申请动态权限——lib库

### 需求

* 所有的模块代码中都会产生权限申请——申请动态权限放在一个透明的Activity中，申请时启动
* 避免重复代码，易扩展与维护——使用AspectJ封装成注解提供使用

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
            // 引入AspectJ的编译器
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
    
    因为apply plugin: 'com.android.library'是library
    所以这里是libraryVariants
    
    如果是apply plugin: 'com.android.application'，要改为applicationVariants
    
    .all是拿到两个变体，一个是debug一个是release
    */
    project.android.libraryVariants.all {
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

    * 请求授权

        ```java
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface Permission {
        
            String[] value();
        
            int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
        }
        ```
        
    * 授权失败

        ```java
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface PermissionCanceled {
        
            int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
        }
        ```
        
    * 授权失败，并勾选不再提示

        ```java
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface PermissionDenied {
        
            int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
        }
        ```

5. 使用AspectJ

    ```java
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
    ```
    
    |参数|含义|
    |:-:|:-:|
    |`()`|表示方法没有任何参数|
    |`(..)`|表示匹配接受任意个参数的方法|
    |`(..,java.lang.String)`|表示匹配接受`java.lang.String`类型的参数结束，且其前边可以接受有任意个参数的方法|
    |`(java.lang.String,..)`|表示匹配接受`java.lang.String`类型的参数开始，且其后边可以接受任意个参数的方法|
    |`(*,java.lang.String)`|表示匹配接受`java.lang.String`类型的参数结束，且其前边接受有一个任意类型参数的方法|
    
    