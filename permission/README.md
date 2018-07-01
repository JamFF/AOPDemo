# 申请动态权限——demo

### 依赖[lib-permission](https://github.com/JamFF/AOPDemo/tree/master/lib-permission)

```groovy
dependencies {
    implementation project(':lib-permission')
}
```

### 申请动态权限

* Activity中使用

    ```java
    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void requestOnePermission() {
        Log.d(TAG, "请求写入权限 Granted");
        Toast.makeText(this, "请求写入权限成功", Toast.LENGTH_SHORT).show();
    }

    @Permission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    private void requestTwoPermission() {
        Log.d(TAG, "请求两个权限 Granted");
        Toast.makeText(this, "请求两个权限成功", Toast.LENGTH_SHORT).show();
    }
 
    @PermissionCanceled()
    private void canceled() {
        Log.d(TAG, "请求权限 Canceled");
        Toast.makeText(this, "请求权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied()
    private void denied() {
        Log.d(TAG, "请求权限 Denied");
        Toast.makeText(this, "请求权限 Denied", Toast.LENGTH_SHORT).show();
    }
    ```

* Fragment中使用

    ```java
    @Permission(Manifest.permission.CAMERA)
    private void requestLocation() {
        Log.d(TAG, "Fragment中请求权限 Granted");
        Toast.makeText(getContext(), "Fragment中请求权限通过", Toast.LENGTH_SHORT).show();
    }

    @PermissionCanceled()
    private void canceled() {
        Log.d(TAG, "Fragment中请求权限 Canceled");
        Toast.makeText(getContext(), "Fragment中请求权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied()
    private void denied() {
        Log.d(TAG, "Fragment中请求权限 Denied");
        Toast.makeText(getContext(), "Fragment中请求权限 Denied", Toast.LENGTH_SHORT).show();
    }
    ```
    
* Service中使用

    ```java
    @Permission(Manifest.permission.CAMERA)
    private void requestCamera() {
        Log.d(TAG, "Service中请求权限 Granted");
        Toast.makeText(this, "Service中请求权限通过", Toast.LENGTH_SHORT).show();
    }

    @PermissionCanceled()
    private void canceled() {
        Log.d(TAG, "Service中请求权限 Canceled");
        Toast.makeText(this, "Service中请求权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied()
    private void denied() {
        Log.d(TAG, "Service中请求权限 Denied");
        Toast.makeText(this, "Service中请求权限 Denied", Toast.LENGTH_SHORT).show();
    }
    ```

### 注意

完成了上述步骤，运行后发现不能申请动态权限，还要在app中引入AspectJ

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
    
### 参考

[Aspect Oriented Programming in Android](https://fernandocejas.com/2014/08/03/aspect-oriented-programming-in-android/)
[Android 基于AOP监控之——AspectJ使用指南](https://blog.csdn.net/woshimalingyi/article/details/51519851)

